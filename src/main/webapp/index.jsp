<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session != null && session.getAttribute("userId") != null) {
        String role = (String) session.getAttribute("userRole");
        if ("ADMIN".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    } else {
        request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
    }
%>
