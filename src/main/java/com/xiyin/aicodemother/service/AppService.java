package com.xiyin.aicodemother.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.xiyin.aicodemother.model.dto.app.AppQueryRequest;
import com.xiyin.aicodemother.model.entity.App;
import com.xiyin.aicodemother.model.entity.User;
import com.xiyin.aicodemother.model.vo.AppVO;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author xiyin-generator
 */
public interface AppService extends IService<App> {

    /**
     * 获取应用封装类
     * @param app
     * @return
     */
    AppVO getAppVO(App app);

    /**
     * 获取查询条件
     * @param appQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    /**
     * 获取应用封装类列表
     * @param appList
     * @return
     */
    List<AppVO> getAppVOList(List<App> appList);


    /**
     * 对话生成应用代码
     * @param appId
     * @param message
     * @param loginUser
     * @return
     */
    Flux<String> chatToGenCode(Long appId, String message, User loginUser);

    /**
     * 部署应用
     * @param appId
     * @param loginUser
     * @return
     */
    String deployApp(Long appId, User loginUser);
}
