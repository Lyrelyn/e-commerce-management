import request from "@/utils/request"

//查询列表数据
export const queryPageApi = (orderId, customerName, page, pageSize) => 
    request.get(`/orders?orderId=${orderId}&customerName=${customerName}&page=${page}&pageSize=${pageSize}`)

//新增
export const addApi = (order) =>  request.post('/orders', order);

//根据ID查询
export const queryInfoApi = (id) =>  request.get(`/orders/${id}`);

//修改
export const updateApi = (order) =>  request.put('/orders', order);

//删除
export const deleteApi = (ids) =>  request.delete(`/orders?ids=${ids}`);