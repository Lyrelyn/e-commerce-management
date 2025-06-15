import request from "@/utils/request"

//列表查询
export const queryAllApi = (brandName, companyName, page, pageSize) => 
    request.get(`/brands?brandName=${brandName}&companyName=${companyName}&page=${page}&pageSize=${pageSize}`)

//添加品牌
export const addBrandApi = (data) => request.post('/brands', data)

//根据ID查询
export const queryInfoApi = (id) => request.get(`/brands/${id}`)

//修改品牌
export const updateBrandApi = (data) => request.put('/brands', data)

//删除品牌
export const deleteBrandApi = (ids) => request.delete(`/brands?ids=${ids}`)