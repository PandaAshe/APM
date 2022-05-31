package com.jiace.apm.core.event

import com.jiace.apm.core.service.SensorData

/**
1 * Copyright (C), 2015-2021, 武汉嘉测科技有限公司 All rights reserved.
2 * Created by wzh on 2022/5/16.
3 * Description:
4 *
5 */

/** 接收到采集仪发来的数据 */
class OnReceiveEndMainData {
    var sensorData = SensorData()
}

/** 自动关闭选择框 */
class AutoCloseChoiceEvent

/** 试验状态发生了变化 */
class TestStatusChanged

/** 数据发生了变化 */
class OnRecordChange
