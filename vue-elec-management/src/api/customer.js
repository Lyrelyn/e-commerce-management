import request from "@/utils/request"

//查询列表数据
export const queryPageApi = (username, gender, page, pageSize) => 
    request.get(`/customers?username=${username}&gender=${gender}&page=${page}&pageSize=${pageSize}`)

//新增
export const addApi = (customer) =>  request.post('/customers', customer);

//根据ID查询
export const queryInfoApi = (id) =>  request.get(`/customers/${id}`);

//修改
export const updateApi = (customer) =>  request.put('/customers', customer);

//删除
export const deleteApi = (ids) =>  request.delete(`/customers?ids=${ids}`);