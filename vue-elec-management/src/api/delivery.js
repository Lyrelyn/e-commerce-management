import request from "@/utils/request"

//查询列表数据
export const queryPageApi = (orderId, courierName, begin, end, page, pageSize) => 
    request.get(`/deliveries?orderId=${orderId}&courierName=${courierName}&begin=${begin}&end=${end}&page=${page}&pageSize=${pageSize}`)

//新增
export const addApi = (delivery) =>  request.post('/deliveries', delivery);

//根据ID查询
export const queryInfoApi = (id) =>  request.get(`/deliveries/${id}`);

//修改
export const updateApi = (delivery) =>  request.put('/deliveries', delivery);

//删除
export const deleteApi = (ids) =>  request.delete(`/deliveries?ids=${ids}`);