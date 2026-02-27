package com.xiyin.aicodemother.core;

import com.xiyin.aicodemother.ai.AiCodeGeneratorService;
import com.xiyin.aicodemother.ai.model.HtmlCodeResult;
import com.xiyin.aicodemother.ai.model.MultiFileCodeResult;
import com.xiyin.aicodemother.core.parser.CodeParserExecutor;
import com.xiyin.aicodemother.core.saver.CodeFileSaverExecutor;
import com.xiyin.aicodemother.exception.BusinessException;
import com.xiyin.aicodemother.exception.ErrorCode;
import com.xiyin.aicodemother.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * AI 代码生成外观类，组合生成和保存功能
 */
@Service
@Slf4j
public class AiCodeGeneratorFacade {

    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    /**
     * 统一入口：根据类型生成并保存代码
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @param appId       应用ID
     * @return 保存的目录
     */
    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> {
                HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMessage);
                yield CodeFileSaverExecutor.executeSaver(htmlCodeResult, CodeGenTypeEnum.HTML, appId);
            }
            case MULTI_FILE -> {
                MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateMultiFileCode(userMessage);
                yield CodeFileSaverExecutor.executeSaver(multiFileCodeResult, CodeGenTypeEnum.MULTI_FILE, appId);
            }
            default -> {
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }


    /**
     * 统一入口：根据类型生成并保存代码(流式)
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @param appId       应用ID
     * @return 保存的目录
     */
    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> {
                Flux<String> result = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
                yield processCodeStream(result, CodeGenTypeEnum.HTML, appId);
            }
            case MULTI_FILE -> {
                Flux<String> result = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
                yield processCodeStream(result,  CodeGenTypeEnum.MULTI_FILE, appId);
            }
            default -> {
                String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }

    /**
     * 通用流式代码处理方法
     * @param codeStream
     * @param codeGenTypeEnum
     * @param appId 应用ID
     * @return
     */
    private Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum codeGenTypeEnum, Long appId){
        // 字符串拼接器，用于当流式返回所有代码之后，再保存代码
        StringBuilder codeBuilder = new StringBuilder();
        return codeStream.doOnNext(chunk -> {
            //实时收集代码片段
            codeBuilder.append(chunk);
        }).doOnComplete(() -> {
            try {
                // 流式返回后保存代码
                String completeHtmlCode = codeBuilder.toString();
                // 使用执行器解析代码
                Object parseObject = CodeParserExecutor.executeParser(completeHtmlCode, codeGenTypeEnum);
                // 使用执行器保存代码
                File saveDir = CodeFileSaverExecutor.executeSaver(parseObject, codeGenTypeEnum, appId);
                log.info("保存成功, 目录为:{}", saveDir.getAbsolutePath());
            } catch (Exception e) {
                log.error("保存失败:{}", e.getMessage());
            }
        });
    }

}
