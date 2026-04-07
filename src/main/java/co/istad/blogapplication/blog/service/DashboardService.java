package co.istad.blogapplication.blog.service;


import co.istad.blogapplication.blog.dto.response.DashboardResponse;

public interface DashboardService {
    DashboardResponse getMyDashboard(String email);
}
