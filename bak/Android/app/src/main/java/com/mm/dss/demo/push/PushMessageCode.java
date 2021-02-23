package com.mm.dss.demo.push;

/**
 * 文件描述：package com.android.business.common; 功能说明： 版权申明：
 * 
 * @author ding_qili
 * @version 2015-3-30下午1:47:25
 */

/**
 * @author 25648
 * 
 */
/**
 * @author 25648
 * 
 */


public enum PushMessageCode {



    /**
     * 人体红外
     */
     PIR ( 0),

    /**
     * 动态监测
     */
     DynamicMonitoring ( 1),

    /**
     * 未知告警
     */
     UnknowAlarm ( 2),

    /**
     * 低电压
     * {"id":848250,"cid":0,"type":0,"time":946660800,"did":"PZC3GW56200039","cname":"SB","remark"
     * :"5%"}
     */
     LowVoltage ( 3),

    /**
     * 配件人体红外
     */
     PIR_ZB ( 4),

    /**
     * 移动感应器发生移动事件（V1.1.1）
     */
     MoveZBb_MoveAlarm ( 5),

    /**
     * 移动感应器长时间未发生移动事件（V1.1.1）
     */
     MoveZB_UnMoveAlarm ( 6),
    /**
     * 配件人体红外检测长久未报警事件（V1.1.1）
     */
     PIR_ZB_LongNoAlarm ( 7),


    /**
     * 配件门磁打开报警
     */
     EG_OPEN ( 8),

    /**
     * 配件门磁防拆报警
     */
     EG_TAMPER ( 9),


    /**
     * 设备通道的上线通知
     */
     Channel_onLine ( 11),


    /**
     * 设备通道的下线通知
     */
     Channel_offLine ( 12),

    /**
     * 开始升级
     */
     Upgrade_start ( 13),

    /**
     * 升级结束
     */
     Upgrade_end ( 14),


    /**
     * 升级失败
     */
     Upgrade_error ( 15),


    /**
     * 升级成功
     */
     Upgrade_success ( 16),


    /**
     * 系统消息
     */
     System_message ( 20),


    /**
     * @deprecated 发现新配件
     */
     Find_New_ZB ( 30),

    /**
     * 智能网关状态变化
     */
     ZB_State_Switch ( 33),

    /**
     * 无SD卡
     */
     SD_Empty ( 50),

    /**
     * SD卡异常
     */
     SD_Error ( 51),


    /**
     * 初始化失败
     */
     SD_Initialize_Failure ( 52),

    /**
     * 初始化成功
     */
     SD_Initialize_Success ( 53),

    /**
     * 初始ing
     */
     SD_Initialize_ing ( 54),


    /**
     * 修改密码
     */
     User_PassWord_Change ( 60),

    /**
     * 添加设备
     */
     Add_Device ( 61),

    /**
     * 删除设备
     */
     Del_Device ( 62),

    /**
     * 发现配件列表
     */
     Find_ZBS ( 63),

    /**
     * 删除配件
     */
     Del_ZB ( 64),

 
    /**
     * 资讯消息
     */
     Information_message ( 81),

    ///////////////////////////////////////////////////////////////////////////////
    DPSDK_CMD_UNKNOWN(0 + Def.DPSDK_CMD_BASE),
    DPSDK_CMD_GENERAL_BEGIN(1+ Def.DPSDK_CMD_BASE),
    DPSDK_CMD_SENDDATATOMDL,					// 通用协议
    DPSDK_CMD_GENERALJSONTRANSPORT,				// JSON通用协议
    DPSDK_CMD_PRELOGIN,							// 查询是否使用加密登陆
    DPSDK_CMD_LOGIN,							// 登录平台通知
    DPSDK_CMD_LOGIN_WITH_ENCRYPTION,			// 加密登录平台通知
    DPSDK_CMD_LOGOUT,							// 登出平台通知
    DPSDK_CMD_GET_GROUP,						// 获取组信息通知(支持增量)
    DPSDK_CMD_GROUP_CHANGE,						// 组织结构变更通知
    DPSDK_CMD_PARKINGLOT_CHANGE,				// 车场信息变更通知

    DPSDK_CMD_CMS_CLOSE,						// CMS连接断开通知(也可能是初次连接不上)
    DPSDK_CMD_GET_OWNERFILE,					// 获取用户文件通知
    DPSDK_CMD_SAVE_OWNERFILE,					// 保存用户文件通知
    DPSDK_CMD_GET_CONFIGINFO,					// 取得配置/参数信息通知
    DPSDK_CMD_SET_SYNCTIME,						// 设置全网校时开关
    DPSDK_CMD_SAVE_OPTLOG,						// 保存操作员端日志通知
    DPSDK_CMD_GET_MENURIGHT,					// 获取模块权限
    DPSDK_CMD_GET_FUNRIGHT,						// 获取指定功能权限
    DPSDK_CMD_LOAD_MCALIINFO,					// 读取双目组织结构定位信息
    DPSDK_CMD_SAVE_MCALIINFO,					// 保存双目组织结构定位信息
    DPSDK_CMD_MCALIINFO_CHANGE,					// 双目组织结构定位信息变更
    DPSDK_CMD_FTP_OPERATOR,						// FTP操作消息
    DPSDK_CMD_GET_USERORGINFO,					// 用户组织结构
    DPSDK_CMD_SHAREVIDEO,						// 视频分享
    DPSDK_CMD_SHAREVIDEO_NOTIFY,				// 视频分享通知
    DPSDK_CMD_USERSTATE_NOTIFY,					// 用户状态通知
    DPSKD_CMD_SAVE_UPLOADPICTRUE,				// 保存上传图片信息
    DPSKD_CMD_DATE_CHANGE_NOTIFY,				// 复用的用户，改变了这个用户的信息
    DPSKD_CMD_USERPSW_CHANGE_NOTIFY,			// 用户密码修改
    DPSDK_CMD_ASKFOR_USER_AUTHORITY,			// 获取用户权限
    DPSDK_CMD_ASKFOR_SYSTEM_TIME,				// 全网校时获取服务时间
    DPSKD_CMD_USERPSW_CHANGE,					// 客户端修改用户密码
    DPSDK_CMD_CHANGE_PASSWORD,					// 修改用户密码

    //华鼎辽宁指挥中心需求--begin
    DPSDK_CMD_QUERY_HDDEVICE_INFO,				// 查询华鼎其他厂商的设备信息		CU->DMS
    DPSDK_CMD_QUERY_HDDEVICE_STATUS,			// 查询华鼎其他厂商的设备状态		CU->DMS
    DPSDK_CMD_QUERY_HDPLATFORM_FLOW,			// 查询华鼎其他厂商的平台流量信息	CU->CMS
    //华鼎辽宁指挥中心需求--end

    DPSDK_CMD_SET_MAXWND,						// 修改用户最大可播放数量
    DPSDK_CMD_PEC_SLEEP_UPDATA ,				// 防瞌睡web信息修改，通知PEC，再次请求
    DPSDK_CMD_SLEEP_ALARM   ,
    DPSDK_CMD_SLEEP_SAVEALARM ,
    DPSDK_CMD_GET_LEADER_INFO,					// 针对陕西建行：获取组员所属的组长列表信息
    DPSDK_CMD_NOTIFY_LEADER_CHANGE,				// 针对陕西建行：组员所属的组长信息变更
    DPSDK_CMD_DIR_SEARCH,						// 目录检索
    DPSDK_CMD_GET_LICENSE_EXPIRE_TIME,			// 获取证书到期时间
    DPSDK_CMD_GET_GROUP_BY_TYPE,				// 根据类型获取组信息通知
    DPSDK_CMD_GET_SERVER_INFO,					// 获取服务信息
    DPSDK_CMD_DSST_GROUP_CHANGE,				// DSST组织结构变更通知消息
    DPSDK_CMD_KANGTEER_NOTIFY,					// 康特尔需求
    DPSDK_CMD_LANGKUN_QUERY_FTPPIC,				// 安徽朗坤ftp图片查询请求
    DPSDK_CMD_LANGKUN_QUERY_FTPPIC_RESPONSE,	// 安徽朗坤ftp图片查询请求返回
    DPSDK_CMD_GET_DEVICE_LIST,					// 获取设备列表信息
    DPSDK_CMD_GET_DEVICES_INFO,					// 获取多个设备的详细信息
    DPSDK_CMD_SOCIAL_ALARM_NOTIFY,				// 社会报警平台通知
    DPSDK_CMD_SOCIAL_SETPOLICE_REQUEST,			// 社会报警平台修改处警状态
    DPSDK_CMD_GENERAL_GET_SERVER_SPM_CONFIG,	// 获取服务SPM配置(IP地址和端口号)
    DPSDK_CMD_GENERAL_IM_REF_CHANGE_NOTIFY,		// 即时通信用户列表相关变更通知，针对即时通信
    DPSDK_CMD_GENERAL_USER_ORG_CHANGED_NOTIFY,	// 用户所在组织结构名称变更，针对即时通信
    DPSDK_CMD_SAVE_ROUTINGTASK,					// 保存视频巡检任务
    DPSDK_CMD_CHANGE_USERPASSWORD,				// 修改用户密码
    DPSDK_CMD_SAVE_UPLOADFILE_INFO,				// 保存文件信息
    DPSDK_CMD_OPERATE_REMOTE_FILE,				// 保存远程文件
    DPSDK_CMD_SYNC_TIME_TOCMS,					// 给CMS校时

    DPSDK_CMD_CUSTOM_BEGIN,
    DPSDK_CMD_CUSTOM_CASENOTIFY,				// 增加余姚定制案件信息通知
    DPSDK_CMD_CUSTOM_END,

    DPSDK_CMD_PEOPLE_UPDATANUM,					// 上报应到人数和实到人数信息

    DPSDK_CMD_GET_SPMCONFIG,					// 江苏高院：获取超级市场的配置
    DPSDK_CMD_GET_EMAP_CAR_ICON_TYPE,			// 获取电子地图车辆图标配置
    DPSDK_CMD_JSON_SEND_TO_CMS,					// 发送给cms的通用json协议
    DPSDK_CMD_JSON_SEND_TO_DMS,					// 发送给dms的通用json协议

    DPSDK_CMD_MESSAGE_REQUEST,					// cms message通知
    DPSDK_CMD_GET_RELATIVE_FPT_PATH,			// 获取ftp相对路径
    DPSDK_CMD_PEC_QUERY_FTPFILELIST,            // 查询FTP文件列表
    DPSDK_CMD_PEC_NOTIFY_MODIFY,                // 通知FTP上传或删除了文件
    DPSDK_CMD_NOTIFYCLIENTSTARTTALK,            //mpt设备请求对讲
    DPSDK_CMD_EXTERNAL_LINKRESOURCE_NOTIFY,		//第三方连接系统 增删改 统一通知消息
    DPSDK_CMD_GET_PE_ALARM_TYPE,				// 获取动环报警类型
    DPSDK_CMD_QUERY_CARDINFO_BY_DEVID,			// 获取卡号信息
    DPSDK_CMD_GET_CHNLID_BY_SITE_CODE,			// 浙江华立通信集团有限公司：通过工地号获取通道ID

    DPSDK_CMD_GENERAL_END(80 + Def.DPSDK_CMD_BASE),

    DPSDK_CMD_MEDIASESSION_BENIN(81 + Def.DPSDK_CMD_BASE),
    DPSDK_CMD_VIDEO_RECEIVE_FIRST_DATA,			// 接收第一帧数据通知
    DPSDK_CMD_VIDEO_RTSP_DISCONNECT,			// RTSP连接断开
    DPSDK_CMD_MEDIASESSION_END(100 + Def.DPSDK_CMD_BASE),

    DPSDK_CMD_REAL_BEGIN(101 + Def.DPSDK_CMD_BASE),
    DPSDK_CMD_OPEN_VIDEO,						// 请求实时视频
    DPSDK_CMD_CLOSE_VIDEO,						// 关闭实时视频
    DPSDK_CMD_PAUSE_VIDEO,						// 暂停RTSP
    DPSDK_CMD_RESUME_VIDEO,						// 恢复RTSP
    DPSDK_CMD_VIDEO_EXPECTION,					// 实时视频异常 通知 fixme：需细化，之后删除
    DPSDK_CMD_VIDEO_LOCK,						// 视频锁定
    DPSDK_CMD_VIDEO_LOCKNOTIFY,					// 视频锁定通知
    DPSDK_CMD_VIDEO_REOCRD,						// 视频录像操作

    DPSDK_CMD_OPEN_VIDEO_EX(150 + Def.DPSDK_CMD_BASE),	            // 请求视频扩展-for new Protocol:28181
    DPSDK_CMD_GET_STREAM_URL,					// 获取视频的url路径
    DPSDK_CMD_CLOSE_STREAM_URL,					// 关闭视频url链接

    DPSDK_CMD_OPEN_VIDEO_MGW,					// 请求视频，MGW去流
    DPSDK_CMD_GET_EXTERNAL_STREAM_URL,			// 获取视频外部url路径
    DPSDK_CMD_REAL_END(200 + Def.DPSDK_CMD_BASE),

    DPSDK_CMD_DEVICE_BEGIN(201 + Def.DPSDK_CMD_BASE),

    DPSDK_CMD_PTZ_BEGIN,
    DPSDK_CMD_PTZ_DIRECTION,					// 云台方向控制
    DPSDK_CMD_PTZ_QUERYPOINT,					// 预置点查询
    DPSDK_CMD_PTZ_QUERYTIMEPOINT,				// 预置点查询(有效时间)
    DPSDK_CMD_PTZ_SETPTZTASK,					// 设置云台定时任务配置
    DPSDK_CMD_PTZ_QUERYPTZTASK,					// 查询云台定时任务配置
    DPSDK_CMD_PTZ_QUERYCRUISE,					// 查询巡航线
    DPSDK_CMD_PTZ_SAVECRUISE,					// 保存巡航线到CMS
    DPSDK_CMD_PTZ_SAVECRUISETODEVICE,			// 保存巡航线到设备
    DPSDK_CMD_PTZ_CAMERAOPERATER,				// 云台变焦，变焦，光圈
    DPSDK_CMD_PTZ_SIT,							// 云台三维定位
    DPSDK_CMD_PTZ_ARRANGE,						// 云台锁定/解锁
    DPSDK_CMD_PTZ_OPENOPER,						// 云台打开关闭（灯光，雨刷，红外线等）
    DPSDK_CMD_PTZ_PREPOINT_OPER,				// 云台 预置点操作(设置，删除，定位)
    DPSDK_CMD_PTZ_TIME_PREPOINT_OPER,			// 云台 预置点操作(设置，删除，定位)(有效期预置点)
    DPSDK_CMD_PTZ_SET_PREPOINT_TIME,			// 设置预置点的有效时间段
    DPSDK_CMD_PTZ_CTRLOUT,						// 联动动作输出
    DPSDK_CMD_PTZ_QUERYALARMOUT,				// 查询报警输出通道开启状态
    DPSDK_CMD_PTZ_QUERYALARMIN,					// 查询报警输入状态
    DPSDK_CMD_PTZ_ALARMINENABLE,				// 报警输入通道启用
    DPSDK_CMD_PTZ_ALARMINREPORT,				// 报警输入状态通知
    DPSDK_CMD_PTZ_QUERYSITINFO,					// 查询云台三维定位信息
    DPSDK_CMD_PTZ_ALARMOUTREPORT,				// 报警输出状态通知
    DPSDK_CMD_FOCUSE_CONTROL,					// 电动聚焦控制
    DPSDK_CMD_QUERY_FOCUSE_STATUE,				// 查询电动聚焦状态
    DPSDK_CMD_PTZ_SITALARMINFO,					// 云台定位报警信息
    DPSDK_CMD_PTZ_QUERYSTATUS,					// 云台状态查询
    DPSDK_CMD_PTZ_SUBPTZALARM,					// 订阅云台报警
    DPSDK_CMD_PTZ_CTRLOUT_RESULT,				// 联动动作输出结果返回
    DPSDK_CMD_PTZ_LOCKSTATUS,					// 云台锁定状态变更通知
    DPSDK_CMD_DEVICE_CLUSTER_STATUS,			// NVR主备状态
    DPSDK_CMD_FISHEYE_SETINFO,					// 鱼眼配置
    DPSDK_CMD_FISHEYE_CONTROL,					// 鱼眼控制
    DPSDK_CMD_ALARMOUT_MODLE,					// 报警输出通道模式
    DPSDK_CMD_PTZ_QUERYPOINT_EX,				// 预置点查询(添加守望点信息)
    DPSDK_CMD_SET_OSDINFO,						// 设置OSD字幕叠加消息
    DPSDK_CMD_PTZ_QUERYIDLE,					// 查询云台空闲动作消息
    DPSDK_CMD_PTZ_SETIDLE,						// 设置云台空闲动作消息
    DPSDK_CMD_PTZ_QUERYPTZPARAM,				// 查询球机云台参数
    DPSDK_CMD_PTZ_SETPTZPARAM,					// 设置球机云台参数
    DPSDK_CMD_PTZ_END,

    DPSDK_CMD_DMS_DISCONNECT,					// 断开DMS服务通知
    DPSDK_CMD_DMS_CONNECT,						// 连接DMS服务通知
    DPSDK_CMD_PTZ_NOTIFY,						// 云台消息通知
    DPSDK_CMD_DEVICE_STATUS_NOTIFY,				// 设备状态通知
    DPSDK_CMD_QUERY_NVRCHNL_STATUS,				// 查询NVR设备通道状态
    DPSDK_CMD_CHNL_STATUS_NOTIFY,				// 通道状态通知
    DPSDK_CMD_NVRCHNL_STATUS,					// NVR设备通道状态
    DPSDK_CMD_DEVICEVIEW_STATUS,				// 查询设备可视域状态
    DPSDK_CMD_CHNLVIEW_STATUS,					// 查询通道可视域状态
    DPSDK_CMD_CHNLVIEW_SET,						// 设置通道可视域
    DPSDK_CMD_REPORT_CHANNELVIEW,				// 可视域信息上报
    DPSDK_CMD_PTZ_QUERYLOTPOINTS,				// 批量预置点查询
    DPSDK_CMD_QUERY_HEAT_MAP,					// 查询前端设备热度图信息
    DPSDK_CMD_DEV_SNAP_PICTURE,					// 前端设备抓图
    DPSDK_CMD_START_GENERATE_WIDEVIEW,			// 开始生产全景图
    DPSDK_CMD_STOP_GENERATE_WIDEVIEW,			// 停止获取全景云台图片
    DPSDK_CMD_GET_WIDEVIEW_STATE,				// 获取全景图读取进度
    DPSDK_CMD_GET_WIDEVIEW_POINTS,				// 根据普通云台坐标获取到全景图云台坐标
    DPSDK_CMD_GET_PTZ_COORDINATE,				// 根据全景图云台坐标获取到普通云台坐标
    DPSDK_CMD_GET_WIDEVIEW_IMGDATA,				// 获取全景图片数据
    DPSDK_CMD_GET_PTZ_STATUS,					// 获取设备当前的位置信息
    DPSDK_CMD_DEVICE_END(300 + Def.DPSDK_CMD_BASE),

    DPSDK_CMD_PLAYBACK_BEGIN(301 + Def.DPSDK_CMD_BASE),
    DPSDK_CMD_QUERY_RECORD,						// 查询录像
    DPSDK_CMD_QUERYDATE_HASRECORD,				// 查询指定时间内有录像的日期（单通道）
    DPSDK_CMD_START_PLAYBACK_FILE,				// 按文件开始回放
    DPSDK_CMD_START_PLAYBACK_TIME,				// 按时间开始回放
    DPSDK_CMD_STOP_PLAYBACK,					// 停止回放
    DPSDK_CMD_SEEK_PLAYBACK,					// 定位回放
    DPSDK_CMD_PAUSE_PLAYBACK,					// 暂停回放
    DPSDK_CMD_RESUME_PLAYBACK,					// 恢复回放
    DPSDK_CMD_SETSPEED_PLAYBACK,				// 设置回放速度
    DPSDK_CMD_PLAYBACK_DATAOVER,				// 收到0包大小的通知
    DPSDK_CMD_QUERY_ALARMRECORD,				// 查询报警录像
    DPSDK_CMD_QUERY_TAGINFO,					// 查询打标信息
    DPSDK_CMD_OPERATOR_TAGINFO,					// 操作打标信息
    DPSDK_CMD_OPERATOR_TAGIMAGE,				// 操作打标图片
    DPSDK_CMD_SS_EXPECTION,						// SS服务异常
    DPSDK_CMD_OPTION_PLAYBACK,					// 保活
    DPSDK_CMD_QUERY_FIRSTRECORD,				// 查询第一段录像开始时间
    DPSDK_CMD_DELETE_PLAYBACK_FILE,
    DPSDK_CMD_GET_PBBYTIME_URL,					// 获取回放URL
    DPSDK_CMD_CLOSE_PBBYTIME_URL,				// 释放回放URL
    DPSDK_CMD_LOCK_RECORD_REST,					// 锁定录像片段
    DPSDK_CMD_UNLOCK_RECORD_REST,				// 解锁录像片段
    DPSDK_CMD_FORCE_UNLOCK_RECORD_REST,			// 强制解锁录像片段,强制解锁后,所有片段锁定计数归0。
    DPSDK_CMD_QUERY_LOCK_RECORD_REST,			// 查询已锁定录像片段
    DPSDK_CMD_CREATE_MOTION_SESSION,			// 创建录像智能分析查询句柄
    DPSDK_CMD_QUERY_MOTION_FRAME,				// 录像智能分析查询
    DPSDK_CMD_CLOSE_MOTION_SESSION,				// 关闭录像智能分析查询句柄
    DPSDK_CMD_QUERY_RECORD_BY_FILE_ID,			// 京东项目定制，根据实时视频文件ID查询录像信息
    DPSDK_CMD_START_PLAYBACK_FILE_RTSPURL,		// 京东项目定制，按RtspUrl开始回放

    DPSDK_CMD_START_PLAYBACK_FILE_EX(350 + Def.DPSDK_CMD_BASE),	    //for new Protocol:28181
    DPSDK_CMD_START_PLAYBACK_TIME_EX,

    DPSDK_CMD_PLAYBACK_END(400 + Def.DPSDK_CMD_BASE),

    DPSDK_CMD_TALK_BEGIN(401 + Def.DPSDK_CMD_BASE),
    DPSDK_CMD_START_TALK,						// 请求语音对讲
    DPSDK_CMD_STOP_TALK,						// 关闭语音对讲
    DPSDK_CMD_PAUSE_TALK,						// 暂停对讲
    DPSDK_CMD_RESUME_TALK,						// 恢复对讲
    DPSDK_CMD_TALK_EXPECTION,					// 对讲异常 通知 fixme：需细化，之后删除
    DPSDK_CMD_OPER_TALK_FILE,					// 对讲录像文件上传下载
    DPSDK_CMD_SAVE_TALK_FILE_INFO,				// 对讲录像文件信息保存
    DPSDK_CMD_QUERY_TALK_FILE_INFO,				// 对讲录像文件查询
    DPSDK_CMD_START_BROADCAST,					// 请求广播Broadcast
    DPSDK_CMD_STOP_BROADCAST,					// 关闭广播
    DPSDK_CMD_VOICE_FILE_PUTIN,                 //语音文件投放
    DPSDK_CMD_VOICE_PUTIN_STOP,                 //语音投放停止
    DPSDK_CMD_TALK_END(450 + Def.DPSDK_CMD_BASE),

    DPSDK_CMD_ALARM_BEGIN(451 + Def.DPSDK_CMD_BASE),
    DPSDK_CMD_GET_SCHEMELIST,					// 获取预案列表
    DPSDK_CMD_GET_SCHEMEFILE,					// 获取单个预案
    DPSDK_CMD_SAVE_SCHEMEFILE,					// 保存单个预案
    DPSDK_CMD_DEL_SCHEMEFILE,					// 删除预案文件
    DPSDK_CMD_GET_TIMETEMPLATE,					// 获取时间模板
    DPSDK_CMD_ADS_LOGIN,						// ADS登陆 fixme:需移到内部
    DPSDK_CMD_ADS_COMMSTATUS,					// ADS交互状态通知
    DPSDK_CMD_REPORT_ALARM,						// 报警上报
    DPSDK_CMD_ALARM_ENABLE,						// 报警使能 CU->ADS
    DPSDK_CMD_QUERY_ALARMCOUNT,					// 查询报警数量 CU->ADS
    DPSDK_CMD_QUERY_ALARM,						// 查询报警信息 CU->ADS
    DPSDK_CMD_SCHEME_INVALIDATE,				// 报警关闭 CU->ADS
    DPSDK_CMD_ALARM_CONFIRM,					// 报警确认 CU->ADS
    DPSDK_CMD_NOTIFY_ALARMMSG,					// 报警业务相关更改通知 CMS->CU
    DPSDK_CMD_QUERY_IVSB_ALARMPIC,				// IVS-B报警图片获取
    DPSDK_CMD_REPORT_PECCANCY_ALARM,			// 围栏报警上报
    DPSDK_CMD_REQUEST_ALARM,					// 报警消息发送 add by minjie 2014-03-28
    DPSDK_CMD_QUERY_DOORINANDOUT,				// 出入门禁消息发送 add by huwenjuan 2014-05-20
    DPSDK_CMD_TRANSFER_ALARM,					// 报警转移
    DPSDK_CMD_CLIENT_ALARM_TO_SERVER,			// 客户端报警到服务
    DPSDK_CMD_QUERY_ZHALARM,					// DSS-P750查询综合报警信息 CU->ADS
    DPSDK_CMD_UPDATE_ALARM_PICPATH,				// 手抓图片路径消息发送
    DPSDK_CMD_ALARM_ACCEPT_CAPACITY,        	// 报警接收能力
    DPSDK_CMD_GET_SHOW_LEVEL_ALARM_TYPE,    	// 获取显示等级报警类型信息
    DPSDK_CMD_GET_CUSTOM_ALARM_TYPE,    		// 获取自定义报警类型信息
    DPSDK_CMD_QUERY_ALLUSEREXTINFO,				// 向ADS获取用户通讯录
    DPSDK_CMD_SENDAPPALARM,						// 由客户端主动向ADS发送报警

    DPSDK_CMD_GET_SYSTEM_ALARM_TYPE(479 + Def.DPSDK_CMD_BASE),	    // 获取自定义报警类型信息

    DPSDK_CMD_REPORT_ALARM_NEW(480 + Def.DPSDK_CMD_BASE),	        // 新报警上报流程
    DPSDK_CMD_REST_GET_TIME_TEMPLATES(481 + Def.DPSDK_CMD_BASE),     // 查询时间模板信息
    DPSDK_CMD_REST_GET_ALARM_PLAN(482 + Def.DPSDK_CMD_BASE),	        // 查询报警预案
    DPSDK_CMD_REST_GET_ALARM_PLAN_OPER(483 + Def.DPSDK_CMD_BASE),	// 报警预案操作
    DPSDK_CMD_QUERY_RFID_ALARM,					// 查询RFID报警
    DPSDK_CMD_ALARM_END(500 + Def.DPSDK_CMD_BASE),

    DPSDK_CMD_INTELLIGENT_BEGIN(501 + Def.DPSDK_CMD_BASE),	        // IVS模块开始
    DPSDK_CMD_GET_IVSRULE,						// 获取智能规则
    DPSDK_CMD_QUERY_IVSPC_CONUT,				// IVS-PC查询总数
    DPSDK_CMD_QUERY_IVSPC_BYPAGE,				// IVS-PC分页查询
    DPSDK_CMD_QUERY_IVSPC_STOP,					// IVS-PC停止查询
    DPSDK_CMD_MANUAL_TRACK,						// ISD手动跟踪
    DPSDK_CMD_MASTERSLAVE_TRACK,				// IVS-M主从手动跟踪
    DPSDK_CMD_FIXEDPOINT_TRACK,					// IVS-M主从定点跟踪
    DPSDK_CMD_INTELLIGENT_END(550 + Def.DPSDK_CMD_BASE),	            // IVS模块结束

    DPSDK_CMD_PEC_BEGIN(551 + Def.DPSDK_CMD_BASE),	                // PEC模块开始
    DPSDK_CMD_PES_ACTIVE_BEGIN,
    DPSDK_CMD_PEC_DOOR_CONTROL,					// 门控制
    DPSDK_CMD_PEC_ALARMHOST_CONFIG,				// 报警主机配置
    DPSDK_CMD_PEC_QUERY_AHOST_ABILITY,			// 查询报警主机能力
    DPSDK_CMD_PEC_QUERY_AHOST_DEFENCE,			// 查询报警主机防区信息
    DPSDK_CMD_PEC_QUERY_DOORINFO,				// 查询门信息
    DPSDK_CMD_PEC_POWERGRID_CONTROL,			// 电网控制
    DPSDK_CMD_PEC_POWERGRID_REPORT,				// 电网设备信息上报
    DPSDK_CMD_PEC_QUERY_DISPACHER_CHNL_STATUS,	// 查询指挥调度设备的通道状态
    DPSDK_CMD_PES_ACTIVE_END,
    DPSDK_CMD_PEC_SAVE_DOORINFO,				// 保存开门记录
    DPSDK_CMD_PEC_QUERY_CARDINFO,				// 获取门禁卡信息
    DPSDK_CMD_PEC_DEVICE_STATUS,				// 设备状态上报
    DPSDK_CMD_PEC_AHOST_REPORT,					// 报警主机状态上报
    DPSDK_CMD_PEC_DOOR_REPORT,					// 门状态上报
    DPSDK_CMD_PEC_SAVE_PICURL,					// 保存门禁图片到ftp
    DPSDK_CMD_PEC_GET_LINKRESOURCE,				// 获取绑定视频资源
    DPSDK_CMD_PEC_LINKRES_CHANGENOTIFY,			// 资源改变通知
    DPSDK_CMD_PEC_LINKRES_CHANGE_RESOURCE,		// 资源改变获取信息
    DPSDK_CMD_PEC_SAVE_ROADGATEINFO,			// 保存开闸记录
    DPSDK_CMD_PEC_QUERY_CARINFO,				// 获取车辆的红黑白名单信息
    DPSDK_CMD_PEC_ROADGATE_CONTROL,				// 道闸控制
    DPSDK_CMD_PEC_SCSDATA_REPORT,				//动环SCS实时数据上报
    DPSDK_CMD_PEC_DISPACHER_REPORT,				// 指挥调度通道状态上报
    DPSDK_CMD_PEC_DOOR_ACCESS_ALARM,            //门禁报警
    DPSDK_CMD_PEC_DOOR_ACCESS_STATUS,           //门禁开关状态
    DPSDK_CMD_PEC_END(600 + Def.DPSDK_CMD_BASE),	                    // PEC模块结束

    DPSDK_CMD_TVWALL_BEGIN(601 + Def.DPSDK_CMD_BASE),	// TVWALL 开始
    DPSDK_CMD_GET_TVWALL_LIST,					// 取得TVWALL列表信息
    DPSDK_CMD_GET_TVWALL_INFO,					// 取得TVWALL xml信息
    DPSDK_CMD_MAPTO_TVWALL,						// 控制大屏
    DPSDK_CMD_DECODER_CONFIG,					// 解码器配置（电视墙配置）
    DPSDK_CMD_TVWALL_SCHEME_CONFIG,				// 预案配置
    DPSDK_CMD_TVWALL_GET_RUN_TASK,				// 获取设备当前任务信息
    DPSDK_CMD_TVWALL_POWERCONTROL,				// 电源控制
    DPSDK_CMD_TVWALL_SET_SIGNAL,				// 设置信号
    DPSDK_CMD_TVWALL_GET_SIGNAL,				// 获取信号
    DPSDK_CMD_TVWALL_NOTIFY,					// tvwall通知消息 CMS->CU
    DPSDK_CMD_TVWALL_SCHEME_LIST,				// 获取任务列表
    DPSDK_CMD_TVWALL_SCHEME_INFO,				// 获取任务信息(或场景信息)
    DPSDK_CMD_TVWALL_DEL_TASK,					// 删除一个任务
    DPSDK_CMD_TVWALL_MODIFY_TASK_BASEINFO,		// 修改任务基本信息（名称或描述）
    DPSDK_CMD_TVWALL_LAYOUT_CFG,				// 电视墙布局
    DPSDK_CMD_TVWALL_SNVD_PORT_SPEED,			// 设置SNVD端口上墙播放速度
    DPSDK_CMD_PIPMAPTO_TVWALL,					// 安徽三联项目定制画中画上墙功能
    DPSDK_CMD_TVWALL_ARRANGE,					// 电视墙锁定
    DPSDK_CMD_TVWALL_ARRANGE_NOTIFY,			// 电视墙锁变更通知
    DPSDK_CMD_TVWALL_RUNINFO,					// 查询电视墙计划运行信息
    DPSDK_CMD_TVWALL_RUNINFO_NOTIFY,			// 电视墙运行信息通知
    DPSDK_CMD_TVWALL_SCREEN_ADDFRAME,			// 屏幕加框
    DPSDK_CMD_TVWALL_CURRENT_TASK,				// 当前电视墙正执行的任务或计划
    DPSDK_CMD_TVWALL_TVWALLINFO_BY_SN,			// 通过sn查询电视墙信息
    DPSDK_CMD_TVWALL_END(650 + Def.DPSDK_CMD_BASE),	                // TVWALL 结束


    DPSDK_CMD_BAY_BEGIN(700 + Def.DPSDK_CMD_BASE),
    DPSDK_CMD_PCS_LOGIN,						// 登录Pcs平台
    DPSDK_CMD_PCS_LOGOUT,						// 登出Pcs平台
    DPSDK_CMD_PCS_CLOSE,						// 与PCS连接断开 通知；触发了OnClose，也可能是初次连接不上
    DPSDK_CMD_BAY_START_MONITOR,				// 打开图片监控
    DPSDK_CMD_BAY_STOP_MONITOR,					// 关闭图片监控
    DPSDK_CMD_BAY_RTPCLOSE_NOTIFY,				// rtp断线通知
    DPSDK_CMD_BAY_DPALARM_NOTIFY,				// 违章报警
    DPSDK_CMD_BAY_WANTED_NOTIFY,				// 布控报警
    DPSDK_CMD_POLICE_SURVEY_NOTIFY,				// 第三方布控
    DPSDK_CMD_BAY_QUERY_PARKINGSTATUS,			// 查询停车信息
    DPSDK_CMD_BAY_PARKINGSTATUS_NOTIFY,			// 通知停车变更的信息
    DPSDK_CMD_BAY_SUBSCRIBE_TRAFFIC_FLOW,		// 交通流量订阅
    DPSDK_CMD_BAY_REPORT_TRAFFIC_FLOW,          // 车道流量上报
    DPSDK_CMD_BAY_REPORT_DEV_TRAFFIC_FLOW,		// 交设备流量上报
    DPSDK_CMD_BAY_SUBSCRIBE_AREA_SPEED,		    // 区间测速订阅
    DPSDK_CMD_BAY_WRITE_TRAFFIC_VIOLATION,		// 违章信息写入
    DPSDK_CMD_BAY_QUERY_TRAFFIC_VIOLATION,		// 违章信息查询
    DPSDK_CMD_BAY_REPORT_SPAN_TEST,             // 区间测速
    DPSDK_CMD_BAY_CARINFO_NOTIFY,				// 进出口车辆通知
    DPSDK_CMD_BAY_QUERYALLWINDING,				// 查询所有线圈信息
    DPSDK_CMD_BAY_MFALARM,						// 硬件故障报警
    DPSDK_CMD_BAY_ABNORMAL_NOTIFY,				// 设备上报图片异常通知

    //人脸卡口
    DPSDK_CMD_BAY_FACEALARM_NOTIFY,		//人脸识别报警通知
    DPSDK_CMD_BAY_FACE_OPT,						//人脸卡口相关操作
    DPSDK_CMD_BAY_GENERAL_NOTIFY,	//通用Json通知
    DPSDK_CMD_BAY_GET_STATUS,
    DPSDK_CMD_BAY_END(750 + Def.DPSDK_CMD_BASE),

    DPSDK_CMD_DEVCONFIG_BEGIN(751 + Def.DPSDK_CMD_BASE),
    DPSDK_CMD_DEVCONFIG_SET,                    // 设备配置设置
    DPSDK_CMD_DEVCONFIG_GET,                    // 设备配置获取
    DPSDK_CMD_DEVCONFIG_END(760 + Def.DPSDK_CMD_BASE),

    DPSDK_CMD_DEVCONFIG_EX_BEGIN(761 + Def.DPSDK_CMD_BASE),
    DPSDK_CMD_DEVCONFIG_SET_EX,                 // 设备配置设置补充
    DPSDK_CMD_DEVCONFIG_GET_EX,                 // 设备配置获取补充
    DPSDK_CMD_DEVCONFIG_OPERATOR_EX,			// 设备配置-设备操作
    DPSDK_CMD_DEVCONFIG_SEARCH_EX,				// 设备配置-设备操作
    DPSDK_CMD_DEVCONFIG_DEVICE_OUT,				// 设备断线通知
    DPSDK_CMD_DEVCONFIG_DEVICE_IN,				// 设备配置-设备上线通知
    DPSDK_CMD_DEVCONFIG_EX_END(770 + Def.DPSDK_CMD_BASE),

    DPSDK_CMD_DEVMGR_BEGIN(900 + Def.DPSDK_CMD_BASE),
    DPSDK_CMD_START_SEARCH_DEVICES,				// 设备搜索
    DPSDK_CMD_DEVICEINFO_BYSEARCH,				// 设备搜索回调
    DPSDK_CMD_STOP_SEARCH_DEVICES,				// 停止设备搜索
    DPSDK_CMD_GET_DEVICE_TEMPINFO,				// 临时获取设备信息
    DPSDK_CMD_DEVALARMOUTPUTSTATE_OPR,			// 设备报警输出通道开关状态操作
    DPSDK_CMD_DEVICE_CAPTUREPIC,				// 抓取通道视频图片（添加设备时使用）
    DPSDK_CMD_DEVMGR_END(1000 + Def.DPSDK_CMD_BASE),

    DPSDK_CMD_EXTRA_M_BEGIN(1001 + Def.DPSDK_CMD_BASE),
    DPSDK_CMD_EXTRA_M_ASKFOR_AREA_INFO,			// 获取电子围栏信息
    DPSDK_CMD_EXTRA_M_ASKFOR_RELATION,			// 获取设备围栏关系
    DPSDK_CMD_EXTRA_M_ASKFOR_AREA_POINTS,		// 获取电子围栏点位信息
    DPSDK_CMD_EXTRA_M_ASKFOR_AREA_RIGHTS,		// 获取围栏权限
    DPSDK_CMD_EXTRA_M_ASKFOR_LAST_GPS_STATUS,	// 获取最后一次GPS信息
    DPSDK_CMD_EXTRA_M_ASKFOR_LAST_DEV_STATUS,	// 获取最后一次设备信息
    DPSDK_CMD_EXTRA_M_ASKFOR_LAST_GAS_STATUS,	// 获取最后一次油耗信息
    DPSDK_CMD_EXTRA_M_ADD_AREA,					// 增加电子围栏
    DPSDK_CMD_EXTRA_M_DEL_AREA,					// 删除电子围栏
    DPSDK_CMD_EXTRA_M_MOD_AREA,					// 修改电子围栏
    DPSDK_CMD_EXTRA_M_UPLOAD_RELATION,			// 上传设备与围栏关系给服务
    DPSDK_CMD_EXTRA_M_ADD_AREA_IN_DEV,			// 增加设备与电子围栏的关联
    DPSDK_CMD_EXTRA_M_DEL_AREA_IN_DEV,			// 删除设备与电子围栏的关联
    DPSDK_CMD_EXTRA_M_NOTIFY_AREA_CHANGE,		// 电子围栏改变通知消息
    DPSDK_CMD_EXTRA_M_NOTIFY_RELATION_CHANGE,	// 关联关系改变通知消息
    DPSDK_CMD_EXTRA_M_SET_OSDINFO,				// OSD字幕叠加消息
    DPSDK_CMD_EXTRA_M_START_RECORD,				// 开始手动录像
    DPSDK_CMD_EXTRA_M_STOP_RECORD,				// 关闭手动录像
    DPSDK_CMD_EXTRA_M_SET_CFGINFO,				// 文本信息
    DPSDK_CMD_EXTRA_M_SNAP_REMOTEDEVICE,		// 远程设备抓图
    DPSDK_CMD_EXTRA_M_NOTIFY_DEVICESNAP,		// 设备抓图回调
    DPSDK_CMD_EXTRA_M_DEVICE_REC_2_PLATFORM,	// 设备上传录像到平台，俄罗斯需求引入
    DPSDK_CMD_EXTRA_M_SET_OSDINFO_EX,			// 南京地铁项目OSD字幕叠加消息((>比DPSDK_CMD_EXTRA_M_SET_OSDINFO多设备id和（还有一个通道号不一样）
    DPSDK_CMD_EXTRA_M_DEVICE_GPS_2_CMS,			// 设备GPS上传给CMS
    DPSDK_CMD_EXTRA_M_GETDEV_ALLCONFIGINFO,	    // 获取设备相关配置信息
    DPSDK_CMD_EXTRA_M_GETDEV_VERSION,			// 获取设备版本
    DPSDK_CMD_EXTRA_M_GETDEV_HARDDISK,			// 获取设备硬盘信息
    DPSDK_CMD_EXTRA_M_GETDEV_ENCODED,			// 获取设备解码信息
    DPSDK_CMD_EXTRA_M_GETDEV_SNAPINFO,			// 获取设备抓图配置信息
    DPSDK_CMD_EXTRA_M_SAVEDEV_ALLCONFIG,		// 设置设备的各类属性
    DPSDK_CMD_EXTRA_M_FORMAT_HARDDIST,			// 格式化设备硬盘分区
    DPSDK_CMD_EXTRA_M_SAVEDEV_ENCODED,			// 设置设备解码信息
    DPSDK_CMD_EXTRA_M_SAVEDEV_SNAPINFO,			// 设置设备抓图配置信息
    DPSDK_CMD_EXTRA_M_CLEARDEVICEALARM,			// 清除设备报警
    DPSDK_CMD_EXTRA_M_GETDEVICE_THIRDSTREAM,	// M获取设备3码流类型
    DPSDK_CMD_EXTRA_M_DEV3GFLOWINFO,			// cms主动发送各个设备3g流量信息
    DPSDK_CMD_EXTRA_M_GETDEVINFOBYREGDEVID,		// 通过设备注册id获取设备信息
    DPSDK_CMD_EXTRA_M_GETUSERINFOLIST,			// 获取用户信息列表
    DPSDK_CMD_EXTRA_M_GETCLASSCHANGEINFO,		// 获取交接班信息
    DPSDK_CMD_EXTRA_M_NOTIFY_USERINFOCHANGE,	// 用户信息改变通知
    DPSDK_CMD_EXTRA_M_SAVECLASSCHANGEMSG,		// 保存交接班信息
    DPSDK_CMD_EXTRA_M_DEVICE_RECORD_UPLOAD_RES,	// 设备录像上传结果通知
    DPSDK_CMD_EXTRA_M_OPERATEOSDTEMPLAT,		// OSD信息模块的增删改
    DPSDK_CMD_EXTRA_M_GETOSDTEMPLATINFO,		// 获取OSD信息模板
    DPSDK_CMD_EXTRA_M_GETHISTORYOSDINFO,		// 获取历史OSD/SMS信息
    DPSDK_CMD_EXTRA_M_SENDSMSINFO,				// 发送短信信息
    DPSDK_CMD_EXTRA_M_GETAREATIMERANGEINFO,		// 获取围栏时间信息
    DPSDK_CMD_EXTRA_M_SETAREATIMERANGEINFO,		// 设置围栏时间信息
    DPSDK_CMD_EXTRA_M_GETDATABASETYPE,			// 获取服务数据库类型
    DPSDK_CMD_EXTRA_M_GETDEV_AUTOCHEKINFO,			//获取设备自检信息
    DPSDK_CMD_EXTRA_M_GETDEV_RECODINFO,					//设备本机录像
    DPSDK_CMD_EXTRA_M_SAVEDEV_RECODINFO,				//保存设备本机录像
    DPSDK_CMD_EXTRA_M_GETOWNLINEINFO,						//获取有权限的线路信息
    DPSDK_CMD_EXTRA_M_GETBUSSCHEDULESINFO,			//获取车辆的排班信息
    DPSDK_CMD_EXTRA_M_GETLASTSTATIONS,						//获取最后到站信息
    DPSDK_CMD_EXTRA_M_NOTITY_SCHEDULE_CHENGED,	//排班信息修改通知
    DPSDK_CMD_EXTRA_M_NOTIFY_SCHEDULESTATE_CHANGED,	//排班状态修改通知
    DPSDK_CMD_EXTRA_M_GETDRIVERMOBILENUMBER,		// 获取司机手机号码
    DPSDK_CMD_EXTRA_M_NOTIFY_DISPITCH_INFO,//调度结果信息
    DPSDK_CMD_EXTRA_M_NOTIFY_CUSTARTPATROL,//开始巡更任务
    DPSDK_CMD_EXTRA_M_RELOAD_VIDEO_PATROL_PLAN,//重新加载巡更计划
    DPSDK_CMD_EXTRA_M_NOTIFY_CUUPDATEREPLYFAST,//快速回复
    DPSDK_CMD_EXTRA_M_END(1100 + Def.DPSDK_CMD_BASE),

    DPSDK_CMD_GBT28181_BEGIN(1101 + Def.DPSDK_CMD_BASE),
    DPSDK_CMD_GBT28181_ADJUST_TIME,
    DPSDK_CMD_GBT28181_GET_DEVICE_STATUS,
    DPSDK_CMD_GBT28181_GET_DEVICE_INFO,
    DPSDK_CMD_GBT28181_START_MANUAL_RECORD,
    DPSDK_CMD_GBT28181_STOP_MANUAL_RECORD,
    DPSDK_CMD_GBT28181_DEVICE_RECOVERY,
    DPSDK_CMD_GBT28181_STOP_RECOVERY,
    DPSDK_CMD_GBT28181_DEVICE_ALARM_RESET,
    DPSDK_CMD_GBT28181_DEVICE_REBOOT,
    DPSDK_CMD_GBT28181_DEVICE_ALARM_NOTIFY,
    DPSDK_CMD_GBT28181_GET_DECODER_DEVICE_STATUS,
    DPSDK_CMD_GBT28181_GET_DECODER_DEVICE_INFO,
    DPSDK_CMD_GBT28181_DECODER_DEVICE_REBOOT,
    DPSDK_CMD_GBT28181_END(1200 + Def.DPSDK_CMD_BASE),

    DPSDK_CMD_SERVER_INFO_BEGIN(1201 + Def.DPSDK_CMD_BASE),
    DPSDK_CMD_SERVER_INFO_QUERY_SERVER_LIST,
    DPSDK_CMD_SERVER_INFO_QUERY_CMS,
    DPSDK_CMD_SERVER_INFO_QUERY_DMS,
    DPSDK_CMD_SERVER_INFO_QUERY_ADS,
    DPSDK_CMD_SERVER_INFO_QUERY_MTS,
    DPSDK_CMD_SERVER_INFO_QUERY_SS,
    DPSDK_CMD_SERVER_INFO_QUERY_VMS,
    DPSDK_CMD_SERVER_INFO_QUERY_PTS,
    DPSDK_CMD_SERVER_INFO_QUERY_PCS,
    DPSDK_CMD_SERVER_INFO_RESTRART,
    DPSDK_CMD_SERVER_INFO_REPORT_STATUS,
    DPSDK_CMD_SERVER_INFO_END(1300 + Def.DPSDK_CMD_BASE),



    //add by jyl
    DPSDK_CMD_PRISON_BEGIN(1401 + Def.DPSDK_CMD_BASE),
    DPSDK_CMD_PRISON_SYNCTIME,                   // 时间同步
    DPSDK_CMD_PRISON_INTEPERINFO,      			 // 审讯人员信息获取
    DPSDK_CMD_PRISON_INTECASEINFO,     			 // 审讯信息
    DPSDK_CMD_PRISON_SAVEINFO,         			 // 保存案件人信息
    DPSDK_CMD_PRISON_SAVEINTEINFO,     			 // 保存审讯信息
    DPSDK_CMD_PRISON_SAVECASEINFO,     			 // 保存案件信息
    DPSDK_CMD_PRISON_DOWNLOADELC,      			 // 下载电子笔记
    DPSDK_CMD_PRISON_GENERALMS,        			 // 通用消息
    DPSDK_CMD_PRISON_GETINTERLABINFO,  			 // 获得审讯标签信息
    DPSDK_CMD_PRISON_GETINTERUSERGRO,            // 获取审讯用户组
    DPSDK_CMD_PRISON_GETSINGCASEINFO,            // 获取单个案例信息
    DPSDK_CMD_PRISON_LASTEINTERSEQ,              // 最近审讯请求
    DPSDK_CMD_PRISON_NOTETEMPLATE,               // 电子模板
    DPSDK_CMD_PRISON_COMMSENTENCE,               // 一般判决
    DPSDK_CMD_PRISON_NOTIFYALL,                  // 所有通报
    DPSDK_CMD_PRISON_LOGININTERROGATION,         // 登录审讯室
    DPSDK_CMD_PRISON_GETNOTETEMPLIST,            // 获得电子模版
    DPSDK_CMD_PRISON_QUERYTAG,					 // 卷宗查询(重点标签)
    DPSDK_CMD_PRISON_ADDTRIALTAG,				 // 审讯打标
    DPSDK_CMD_PRISON_SXFTP,						 // 审讯FTP上传下载

    DPSDK_CMD_PRISON_GETDEVBURNERINFO,			 // 获取刻录主机信息
    DPSDK_CMD_PRISON_SETDEVBURNERHEADER,		 // 设置刻录片头设置
    DPSDK_CMD_PRISON_CONTROLDEVBURNER,			 // 控制刻录主机
    DPSDK_CMD_PRISON_GETBURNERCDSTATE,			 // 获取刻录实时状态信息
    DPSDK_CMD_PRISON_GETENCODEPLAN,				 // 获取编码计划
    DPSDK_CMD_PRISON_SETENCODEPLAN,				 // 设置编码计划
    DPSDK_CMD_PRISON_SETBURNRECORDFORMAT,		 // 刻录格式设置
    DPSDK_CMD_PRISON_BRUNSTATUS_NOTIFY,			 // 刻录状态主动通知
    DPSDK_CMD_PRISON_SAVEBURNPARAM,				 // 保存刻录参数
    DPSDK_CMD_PRISON_QUERYBURNPARAM,			 // 查询刻录参数
    DPSDK_CMD_PRISON_VOICE_IMPEL,				 // 语音激励
    DPSDK_CMD_PRISON_DVD_CONTROL,				 // DVD控制
    DPSDK_CMD_PRISON_SETCOMBINEDSCREEN,			 // 设置组合屏信息
    DPSDK_CMD_PRISON_GETCOMBINEDSCREEN,			 // 获取组合屏信息
    DPSDK_CMD_PRISON_MANAGEBROADCAST_NOTIFY,	 // 播控管理消息通知
    DPSDK_CMD_PRISON_COURTSNAP,					 // 请求庭审抓图
    DPSDK_CMD_PRISON_GETDISKINFO,				 // 获取设备磁盘容量
    DPSDK_CMD_PRISON_END(1500 + Def.DPSDK_CMD_BASE),
    //end by jyl

    DPSDK_CMD_EXTRA_A_BEGIN(1501 + Def.DPSDK_CMD_BASE),
    DPSDK_CMD_GET_ORG_TREE,						// 获取业务组织
    DPSDK_CMD_NOTIFY_ORG_TREE,					// 通知业务组织变更
    DPSDK_CMD_GET_ALL_DOMAIN_INFO,				// 获取全部域信息
    DPSDK_CMD_NOTIFY_ALL_DOMAIN_INFO,			// 通知域信息变更
    DPSDK_CMD_NOTIFY_DOMAIN_STATUS,				// 通知域状态变更
    DPSDK_CMD_GET_USERCAMERAID,					// 获取视频资源绑定
    DPSDK_CMD_NOTIFY_RELATION,					// 通知视频资源变更
    DPSDK_CMD_TO_CU,							// 对讲交互
    DPSDK_CMD_TALK_LOG,							// 对讲日志
    DPSDK_CMD_SHARE_RTSPURL,					// 分享RTSP
    DPSDK_CMD_NOTIFY_RTSPURL,					// 通知RTSP
    DPSDK_CMD_PLAY_RTSPURL,						// 播放RTSP
    DPSDK_CMD_TEARDOWN_RTSPURL,					// 断开RTSP
    DPSDK_CMD_GET_MENUTOOL,						// 获取菜单导航
    DPSDK_CMD_GET_NEWS,							// 获取新闻
    DPSDK_CMD_NOTIFY_NEWS,						// 新闻通知
    DPSDK_CMD_NOTIFY_MOD_NAVIGATION,            // 通知修改导航栏
    DPSDK_CMD_EXTRA_A_END(1600 + Def.DPSDK_CMD_BASE),

    //add by djm 20131025 报警运营平台增加
    DPSDK_CMD_ALARM_BUSINESS_BEGIN(1601 + Def.DPSDK_CMD_BASE),
    DPSDK_CMD_CHN_ALARMTYPE_QUERY,              // 报警类型查询
    DPSDK_CMD_ALARM_HOST_CONTROL,				// 报警主机控制 add by minjie 2013-11-29
    DPSDK_CMD_CUSTOMERINFO_QUERY,				// 客户资料(联系方式)查询
    DPSDK_CMD_CUSTOMERINFO_NOTIFY,				// 客户资料(及联系方式)增删改通知
    DPSDK_CMD_VIDEOALARMHOST_QUERY,				// 视频报警主机查询
    DPSDK_CMD_VIDEOALARM_QUERY,					// 视频报警主机报警查询
    DPSDK_CMD_REPORT_ALARMHOST_STATUS,			// DMS 报警主机布防、撤防、旁路与取消旁路状态上报 add by zxb 12-4-2013
    DPSDK_CMD_DEFENDCUSTOMER_QUERY,				// 布撤防客户查询
    DPSDK_CMD_ONLINECUSTOMER_QUERY,				// 在线离线客户查询
    DPSDK_CMD_SERVICETIMEOUT_NOTIFY,			// 保险-服务到期提醒
    DPSDK_CMD_VIDEOALARMCOUNT_QUERY,			// 报警查询总数添加
    DPSDK_CMD_QUERY_ALARMREDEAL_TIME,			// 获取二次处理时间间隔
    DPSDK_CMD_SENDMESSAGE,						// 短信发送
    DPSDK_CMD_QUERY_ONLNEUSER_BY_ROLE,			// 查询同角色在线用户
    DPSDK_CMD_WRITE_CASE_INFO,					// 立案信息写入
    DPSDK_CMD_QUERY_110_ONLINEUSER_ROLE,		// 查询110用户列表
    DPSDK_CMD_QUERY_ALL_USERS,					// 查询所有用户列表
    DPSDK_CMD_UPLOAD_LOG_ONDUTY,				// 上传值班日志信息
    DPSDK_CMD_QUERY_USER_DUTY_LOGIN,			// 查询用户签入or签出
    DPSDK_CMD_VIDEOALARM_QUERY_NEW,				// 视频报警主机报警查询
    DPSDK_CMD_VIDEOALARMCOUNT_QUERY_NEW,		// 报警查询总数添加
    DPSDK_CMD_PHONE_SUBSCRIBE_ALARM,			// 手机订阅报警
    DPSDK_CMD_ALARM_BUSINESS_END(1700 + Def.DPSDK_CMD_BASE),
    //end by djm

    DPSDK_CMD_FACE_MANAGE_BEGIN(1701 + Def.DPSDK_CMD_BASE),
    DPSDK_CMD_EXTRACT_FACEPIC,					// 通过大图获取人脸图片
    DPSDK_CMD_OPERATE_FACE_LIB,					// 添加/修改/删除 人脸识别布控（黑白名单）
    DPSDK_CMD_QUERY_FACE_COUNT,					// 查询匹配的人脸数量(黑白名单，历史库)
    DPSDK_CMD_QUERY_FACE_DATA,					// 请求具体匹配数据(黑白名单，历史库)
    DPSDK_CMD_STOP_FACE_QUERY,					// 停止当前人脸库查询会话
    DPSDK_CMD_QUERY_IVSFALARM_COUNT,			// 查询人脸报警数量
    DPSDK_CMD_QUERY_IVSFALARM_DATA,				// 请求人脸报警具体数据
    DPSDK_CMD_STOP_IVSFALARM_QUERY,				// 停止人脸报警查询会话
    DPSDK_CMD_GET_IVSFALARM_PIC,				// 获取人脸报警指定的全景图片
    DPSDK_CMD_RECONGNITION_ATTENDANCE,			// 添加/修改/删除 人脸识别布控（黑白名单）
    DPSDK_CMD_QUERY_STATUE_NOTIFY,				// 人脸查询状态通知
    DPSDK_CMD_QUERY_PROGRESS,					// 查询进度上报
    DPSDK_CMD_FACE_MANAGE_END(1800+ Def.DPSDK_CMD_BASE),

    DPSDK_CMD_DIAGNOSE_BEGIN(1801 + Def.DPSDK_CMD_BASE),
    DPSDK_CMD_QUERY_DAIGNOSECOUNT,				// 查询视频质量诊断异常数
    DPSDK_CMD_GET_DIAGNOSEDATE,					// 获取视频质量诊断异常结果
    DPSDK_CMD_STOP_DIAGNOSE,					// 停止获取结果
    DPSDK_CMD_QUERY_DIAGNOSECHN_DATE,			// 查询通道视频诊断结果
    DPSDK_CMD_QUERY_DIAGNOSECHN_COUNT,			// 查询通道历史诊断数
    DPSDK_CMD_GET_DIAGNOSECHN_DATE,				// 获取通道诊断结果
    DPSDK_CMD_STOP_DIAGNOSECHN,					// 停止获取通道结果
    DPSDK_CMD_DIAGNOSE_END(1900 + Def.DPSDK_CMD_BASE),

    DPSDK_CMD_REPORT_IVSPC_BEGIN(1901 + Def.DPSDK_CMD_BASE),
    DPSDK_CMD_REPORT_IVSPC_COUNT,				// 上报IVS-PC人数按组统计
    DPSDK_CMD_GET_IVSPC_COUNT,					// 查询IVS-PC人数按组统计
    DPSDK_CMD_REPORT_IVSPC_END(1904 + Def.DPSDK_CMD_BASE),

    DPSDK_CMD_POWERENERGY_BEGIN(2000 + Def.DPSDK_CMD_BASE),
    //DPSDK_CMD_QUERY_DATACOUNT,				// 获取查询数据数量
    //DPSDK_CMD_QUERY_HISTORY_DATA,				// 获取仪表历史信息
    //DPSDK_CMD_STOP_QUERY,						// 停止获取数据
    DPSDK_CMD_POWERENERGY_DATAREPORT,			// 动环实时数据上报
    //DPSDK_CMD_POWERENERGY_DATAREPOET_NVS,		// NVS动环实时数据上报DMS->CU

    DPSDK_CMD_POWENERGY_PES_ACTIVE_BEGIN,		// cu->pes 动环控制命令走pes
    DPSDK_CMD_POWERENERGY_CONTROL,				// 动环开关控制
    DPSDK_CMD_POWERENERGY_GETSTATUS,			// 获取动环当前开或者关的状态
    DPSDK_CMD_POWENERGY_PES_ACTIVE_END,			//
    DPSDK_CMD_POWERENERGY_QUERY_PIC,			// 查询动环图片
    DPSDK_CMD_POWERENERGY_DATA_COUNT,			// 查询动环历史数据数量
    DPSDK_CMD_POWERENERGY_HISTORY_DATA,			// 查询动环历史数据CU->CMS
    DPSDK_CMD_POWERENERGY_END(3000 + Def.DPSDK_CMD_BASE),

    DPSDK_CMD_SCS_BEGIN(3001 + Def.DPSDK_CMD_BASE),
    DPSDK_CMD_SCS_LOGIN,						// 登录Scs平台
    DPSDK_CMD_START_CALL,						// 发起呼叫
    DPSDK_CMD_STOP_CALL,						// 停止呼叫
    DPSDK_CMD_CEASE_CALL,						// 释放呼叫
    DPSDK_CMD_INVITE_CALL,						// 呼叫邀请
    DPSDK_CMD_BYE_CALL,							// 呼叫被中断
    DPSDK_CMD_MODIFY_CALL_STATUS,				// 修改呼叫状态
    DPSDK_CMD_CALL_EXPECTION,					// 呼叫异常
    DPSDK_CMD_SCS_MESSAGE_NOTIFY(3012 + Def.DPSDK_CMD_BASE),				// 接收SCS服务的主动通知
    DPSDK_CMD_CANCEL_VT_CALL(3018 + Def.DPSDK_CMD_BASE),					// 主叫方取消呼叫
    DPSDK_CMD_BYE_VT_CALL(3019 + Def.DPSDK_CMD_BASE),					    // 主叫方挂断呼叫通知
    DPSDK_CMD_CALL_INVITE_NOTIFY(3025 + Def.DPSDK_CMD_BASE),				// 接收呼叫通知
    DPSDK_CMD_VT_CALL_STOP_NOTIFY(3026 + Def.DPSDK_CMD_BASE),				// 呼叫挂断通知
    DPSDK_CMD_CALL_STOP_NOTIFY,					// 呼叫挂断通知

    DPSDK_CMD_SCS_MESSAGE_TEXT,					// 发送文字
    DPSDK_CMD_SCS_TEXT_NOTIFY,					// 接收SCS服务文字

    DPSDK_CMD_START_VT_CALL,					// 可视对讲发起呼叫
    DPSDK_CMD_STOP_VT_CALL,						// 可视对讲停止呼叫
    DPSDK_CMD_BUSY_VT_CALL,						// 可视对讲呼叫繁忙

    DPSDK_CMD_REJECT_VT_CALL,					// 被叫方拒绝呼叫
    DPSDK_CMD_CEASE_VT_CALL,					// 释放可视对讲呼叫
    DPSDK_CMD_INVITE_VT_CALL,					// 可视对讲呼叫邀请
    DPSDK_CMD_MODIFY_VT_CALL_STATUS,			// 可视对讲修改呼叫状态
    DPSDK_CMD_VT_CALL_EXPECTION,				// 可视对讲呼叫异常
    DPSDK_CMD_VT_CALL_INVITE_NOTIFY,			// 可视对讲接收呼叫通知

    DPSDK_CMD_VT_CALL_MESSAGE,					// 信息分发和快递通知

    DPSDK_CMD_RING_CALL,						// 发送响铃
    DPSDK_CMD_RING_NOTIFY,						// 响铃通知
    DPSDK_CMD_SCS_LOGOUT(3030 + Def.DPSDK_CMD_BASE),	// 登出
    DPSDK_CMD_SCS_END(3100 + Def.DPSDK_CMD_BASE),

    DPSDK_CMD_MESSAGE_NEW_ORG_BEGIN(3101 + Def.DPSDK_CMD_BASE) ,
    DPSDK_CMD_MESSAGE_GET_ALL_ORG_TREE,			// 加载组织树
    DPSDK_CMD_MESSAGE_ADD_ORG,					// 增加组织节点
    DPSDK_CMD_MESSAGE_MODIFY_ORG,				// 修改组织节点
    DPSDK_CMD_MESSAGE_DELETE_ORG,				// 删除组织节点
    DPSDK_CMD_MESSAGE_GET_DEVICE_INFO,			// 获取设备信息
    DPSDK_CMD_MESSAGE_ADD_DEVICE,				// 增加设备
    DPSDK_CMD_MESSAGE_MODIFY_DEVICE,			// 修改设备
    DPSDK_CMD_MESSAGE_DELETE_DEVICE,			// 删除设备
    DPSDK_CMD_MESSAGE_USER_ROLE_CHANGED,		// 用户角色变更
    DPSDK_CMD_MESSAGE_ROLE_ORG_CHANGED,			// 角色组织变更
    DPSDK_CMD_MESSAGE_LOGIC_ORG_CHANGED,		// 逻辑组织变更
    DPSDK_CMD_MESSAGE_CHANNEL_RIGHT_CHANGED,	// 通道权限变更
    DPSDK_CMD_MESSAGE_CODEINFO_CHANGED,			// 节点<通道或设备>所属组织及排序变更
    DPSDK_CMD_MESSAGE_GETGROUP_SNAPSHOT,		// 获取组织快照
    DPSDK_CMD_MESSAGE_GETDEVICE_DETAIL,			// 获取设备详细信息
    DPSDK_CMD_MESSAGE_GETUSERS,					// 获取用户信息
    DPSDK_CMD_MESSAGE_GETORG_DETAIL,			// 获取组织详细信息
    DPSDK_CMD_MESSAGE_GETORG_BY_REGID,			// 京东定制，通过注册ID获取组织树
    DPSDK_CMD_MESSAGE_NEW_ORG_END(3200 + Def.DPSDK_CMD_BASE),

    //IP对讲 add by huwenjuan
    DPSDK_CMD_IPTALK_BEGIN(3201 + Def.DPSDK_CMD_BASE),
    DPSDK_CMD_IPTALK_INVITE(3202 + Def.DPSDK_CMD_BASE),		        // 设备发起对讲
    DPSDK_CMD_IPTALK_HANGUP(3203 + Def.DPSDK_CMD_BASE),		        // 设备挂断对讲
    DPSDK_CMD_IPTALK_IGNORE(3204 + Def.DPSDK_CMD_BASE),              // 忽略对讲
    DPSDK_CMD_IPTALK_END(3300 + Def.DPSDK_CMD_BASE),


    DPSDK_CMD_REST_TREE_BEGIN(3801 + Def.DPSDK_CMD_BASE),            //RestSDK BRM服务相关接口
    DPSDK_CMD_REST_TREE_GET_VERSION(3802 + Def.DPSDK_CMD_BASE),     //查询资源版本
    DPSDK_CMD_REST_ADSEVENT_SUBSCRIBE(3803 + Def.DPSDK_CMD_BASE),   //订阅
    DPSDK_CMD_REST_ADSEVENT_UNSUBSCRIBE(3804 + Def.DPSDK_CMD_BASE), //取消订阅
    DPSDK_CMD_REST_ADSEVENT_PULLEVENT(3805 + Def.DPSDK_CMD_BASE),
    DPSDK_CMD_REST_GET_ORGANIZATION(3806 + Def.DPSDK_CMD_BASE),     //组织
    DPSDK_CMD_REST_GET_DEVICE(3807 + Def.DPSDK_CMD_BASE),           //设备
    DPSDK_CMD_REST_GET_ENCUNIT(3808 + Def.DPSDK_CMD_BASE),          //编码单元
    DPSDK_CMD_REST_GET_ENCCHNL(3809 + Def.DPSDK_CMD_BASE),          //编码通道
    DPSDK_CMD_REST_GET_DECUNIT(3810 + Def.DPSDK_CMD_BASE),          //解码单元
    DPSDK_CMD_REST_GET_DECCHNL(3811 + Def.DPSDK_CMD_BASE),          //解码通道
    DPSDK_CMD_REST_GET_ALARMIN_UNIT(3812 + Def.DPSDK_CMD_BASE),     //报警输入单元
    DPSDK_CMD_REST_GET_ALARMIN_CHNL(3813 + Def.DPSDK_CMD_BASE),     //报警输入通道
    DPSDK_CMD_REST_GET_ALARMOUT_UNIT(3814+ Def.DPSDK_CMD_BASE),     //报警输出单元
    DPSDK_CMD_REST_GET_ALARMOUT_CHNL(3815+ Def.DPSDK_CMD_BASE),     //报警输出通道
    DPSDK_CMD_REST_GET_ACCESSCTRL_UNIT(3816+ Def.DPSDK_CMD_BASE),     //门禁单元
    DPSDK_CMD_REST_GET_ACCESSCTRL_CHNL(3817+ Def.DPSDK_CMD_BASE),     //门禁通道
    DPSDK_CMD_REST_GET_SCREENIN_UNIT(3818+ Def.DPSDK_CMD_BASE),     //电视墙输入单元
    DPSDK_CMD_REST_GET_SCREENIN_CHNL(3819+ Def.DPSDK_CMD_BASE),     //电视墙输入通道
    DPSDK_CMD_REST_GET_SCREENOUT_UNIT(3820+ Def.DPSDK_CMD_BASE),     //电视墙输出单元
    DPSDK_CMD_REST_GET_SCREENOUT_CHNL(3821+ Def.DPSDK_CMD_BASE),     //电视墙输出通道
    DPSDK_CMD_REST_GET_LED_UNIT(3822+ Def.DPSDK_CMD_BASE),     //LED单元
    DPSDK_CMD_REST_GET_LED_CHNL(3823+ Def.DPSDK_CMD_BASE),     //LED通道
    DPSDK_CMD_REST_GET_GROUP(3824+ Def.DPSDK_CMD_BASE),     //获取子组织，子设备和所有子通道
    DPSDK_CMD_REST_GET_DEVICE_STATUS(3825+ Def.DPSDK_CMD_BASE),     //获取设备状态
    DPSDK_CMD_REST_GET_ENCCHNL_STATUS(3826+ Def.DPSDK_CMD_BASE),     //获取编码通道状态
    DPSDK_CMD_REST_TREE_END(4000+ Def.DPSDK_CMD_BASE),     //RestSDK BRM服务相关接口

    DPSDK_CMD_GENERAL_EXTRA_BEGIN(4101+ Def.DPSDK_CMD_BASE),
    DPSDK_CMD_MODIFY_LOGO(4102+ Def.DPSDK_CMD_BASE),		//修改图标通知
    DPSDK_CMD_MODIFY_MENURIGHT(4103+ Def.DPSDK_CMD_BASE),		//客户端模块权限修改
    DPSDK_CMD_RECONNECT_TO_CMS(4104+ Def.DPSDK_CMD_BASE),		//重连CMS
    DPSDK_CMD_QUERY_DEVSERVER(4105+ Def.DPSDK_CMD_BASE),		//查询设备对应服务信息
    DPSDK_CMD_GENERAL_EXTRA_END(4200+ Def.DPSDK_CMD_BASE),

    DPSDK_CMD_END(5000+ Def.DPSDK_CMD_BASE),

    DPSDK_CMD_REPORT_ALARM_PICTURE(5000+ Def.DPSDK_CMD_BASE + 1); //图片报警

    private int value;

    private PushMessageCode(){
        this(Counter.nextValue);
    }

    private PushMessageCode(int value){
        this.value = value;
        Counter.nextValue =  value + 1;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    private static class Counter{
        private static int nextValue = 0;
    }

    public static final String parse2Value(PushMessageCode cmd){
        if(cmd == null){
            return "";
        }
        return String.valueOf(cmd.getValue());
    }

    public static PushMessageCode valueOf(int value){
        for (PushMessageCode eventCmd:PushMessageCode.values()) {
            if(value == eventCmd.getValue()){
                return eventCmd;
            }
        }
        return null;
    }

    static class  Def{
        static final int DPSDK_CMD_BASE = 100000;
    }

}
