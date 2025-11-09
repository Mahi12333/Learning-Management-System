<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>OTP Verification</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f8f9fa; margin: 0; padding: 0; }
        .container { max-width: 600px; margin: auto; background: #fff; border-radius: 8px; overflow: hidden; }
        .header { background-color: #004080; color: #fff; padding: 20px; text-align: center; }
        .content { padding: 20px; color: #333; text-align: center; }
        .otp { font-size: 28px; font-weight: bold; color: #004080; margin: 20px 0; }
        .footer { background-color: #f1f1f1; color: #555; padding: 10px; text-align: center; font-size: 12px; }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h2>Library Management System 📚</h2>
    </div>
    <div class="content">
        <p>Hello <strong th:text="${name}">User</strong>,</p>
        <p>Your One-Time Password (OTP) for verification is:</p>
        <div class="otp" th:text="${otp}">123456</div>
        <p>This OTP will expire in <strong>5 minutes</strong>.</p>
    </div>
    <div class="footer">
        <p>© 2025 Library Management System. All rights reserved.</p>
        <p>If you didn’t request this email, please ignore it.</p>
    </div>
</div>
</body>
</html>
