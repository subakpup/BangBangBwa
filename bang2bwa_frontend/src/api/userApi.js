import axios from "axios";

const BASE_URL = 'http://localhost:8080/users';

// POST /singup (Body: SignupRequestDto)
export const signup = async (request) => {
    try {
        const response = await axios.post(`${BASE_URL}/signup`, request);
        return response.data;
    } catch (error) {
        return error;
    }
};

// GET /email-verification/duplicate
export const checkEmail = async (email) => {
    try {
        const response = await axios.get(`${BASE_URL}/email-verification/duplicate`, {
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
        const response = await axios.post(`${BASE_URL}/email-verification/request`, null, {
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
        const response = await axios.post(`${BASE_URL}/email-verification/verify`, null, {
            params: { email, code }
        });
        return response.data;
    } catch (error) {
        return error;
    }
};