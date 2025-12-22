import { api } from "@/api/index";

export const getSidoList = async () => {
    const response = await api.get('/adress', {
        params: { action: 'sido' }
    });
    return response.data;
};

export const getSggList = async (sidoCode) => {
    const response = await api.get('/address', {
        params: {
            action: 'sgg',
            sidoCode: sidoCode
        }
    });
    return response.data;
}