import { api } from "@/api/index";

// POST /singup (Body: SignupRequestDto)
export const signup = async (request) => {
    try {
        const response = await api.post('/users/signup', request);
        return response.data;
    } catch (error) {
        return error;
    }
};

// GET /email-verification/duplicate
export const checkEmail = async (email) => {
    try {
        const response = await api.get('/users/email-verification/duplicate', {
            params: { email }
        });
        return response.data;
    } catch (error) {
        return error;
    }
};

// POST /email-verification/request
export const sendEmailVerification = async (email) => {
    try {
        const response = await api.post('/users/email-verification/request', null, {
            params: { email }
        });
        return response.data;
    } catch (error) {
        return error;
    }
};

// POST /email-verification/verify
export const verifyEmail = async (email, code) => {
    try {
        const response = await api.post('/users/email-verification/verify', null, {
            params: { email, code }
        });
        return response.data;
    } catch (error) {
        return error;
    }
};

// POST /login (body: LoginRequestDto)
export const login = async (request) => {
    const response = await api.post('/users/login', request);
    return response.data;
};

// POST /logout
export const logout = async () => {
    try {
        const response = await api.post('/users/logout');
        return response.data;
    } catch (error) {
        return error;
    }
};