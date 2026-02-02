package com.xiyin.aicodemother.core.saver;

import cn.hutool.core.util.StrUtil;
import com.xiyin.aicodemother.ai.model.HtmlCodeResult;
import com.xiyin.aicodemother.exception.BusinessException;
import com.xiyin.aicodemother.exception.ErrorCode;
import com.xiyin.aicodemother.model.enums.CodeGenTypeEnum;

import java.io.File;

public class HtmlCodeFileSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult>{
    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected void saveFiles(HtmlCodeResult result, String baseDirPath) {
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
    }

    @Override
    protected void validateInput(HtmlCodeResult result) {
        super.validateInput(result);
        if(StrUtil.isBlank(result.getHtmlCode())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "HTML代码不能为空");
        }
    }
}
