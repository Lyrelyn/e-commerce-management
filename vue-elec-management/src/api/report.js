import request from '@/utils/request'

//统计客户性别信息
export const queryCustomerGenderDataApi = () => request.get(`/report/customerGenderData`)

//统计客户等级信息
export const queryCustomerLevelDataApi = () => request.get(`/report/customerLevelData`)

export const queryTop5ProductSalesApi = () => request.get(`/report/commoditySaleData`)