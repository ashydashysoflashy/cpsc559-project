import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api", // should set to actual backend URL (and then proxy when ready)
});

export default api;
