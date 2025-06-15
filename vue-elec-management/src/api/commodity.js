import request from "@/utils/request"

//查询列表数据
export const queryPageApi = (name, minPrice, maxPrice, page, pageSize) => 
    request.get(`/commodities?name=${name}&minPrice=${minPrice}&maxPrice=${maxPrice}&page=${page}&pageSize=${pageSize}`)
  //&minPrice=${minPrice}&page=${page}&pageSize=${pageSize}

//新增
export const addApi = (commodity) =>  request.post('/commodities', commodity);

//根据ID查询
export const queryInfoApi = (id) =>  request.get(`/commodities/${id}`);

//修改
export const updateApi = (commodity) =>  request.put('/commodities', commodity);

//删除
export const deleteApi = (ids) =>  request.delete(`/commodities?ids=${ids}`);