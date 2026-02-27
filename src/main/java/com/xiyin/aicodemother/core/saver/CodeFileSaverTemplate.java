package com.xiyin.aicodemother.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.xiyin.aicodemother.exception.BusinessException;
import com.xiyin.aicodemother.exception.ErrorCode;
import com.xiyin.aicodemother.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 抽象代码文件保存器 - 模板方法模式
 */
public abstract class CodeFileSaverTemplate<T> {

    // 文件保存根目录
    private static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir") + File.separator + "tmp" + File.separator + "code_output";

    public final File saveCode(T result, Long appId){
        // 验证输入
        validateInput(result);
        // 构建唯一目录
        String baseDirPath = buildUniqueDir(appId);
        // 保存文件
        saveFiles(result, baseDirPath);
        // 返回目录文件对象
        return new File(baseDirPath);
    }

    protected void validateInput(T result){
        if(result == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"代码结果对象不能为空");
        }
    }

    /**
     * 构建文件唯一路径(tmp/code_output/bizType_雪花ID)
     * @param appId 应用ID
     * @return
     */
    protected final String buildUniqueDir(Long appId){
        String bizType = getCodeType().getValue();
        String uniqueDirName = StrUtil.format("{}_{}", bizType, appId);
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 写入单个文件
     * @param dirPath
     * @param fileName
     * @param content
     */
    protected final void writeToFile(String dirPath, String fileName, String content){
        String filePath = dirPath + File.separator + fileName;
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }

    /**
     * 获取代码类型(由子类实现)
     * @return
     */
    protected abstract CodeGenTypeEnum getCodeType();

    /**
     * 保存文件的具体实现(由子类实现)
     * @param result
     * @param baseDirPath
     */
    protected abstract void saveFiles(T result, String baseDirPath);
}
