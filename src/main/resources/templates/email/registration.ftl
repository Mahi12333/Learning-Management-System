<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Welcome to LMS</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f8f9fa; margin: 0; padding: 0; }
        .container { max-width: 600px; margin: auto; background: #fff; border-radius: 8px; overflow: hidden; }
        .header { background-color: #004080; color: #fff; padding: 20px; text-align: center; }
        .content { padding: 20px; color: #333; }
        .btn { display: inline-block; background-color: #004080; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; margin-top: 15px; }
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
        <p>Welcome to our Library Management System! 🎉</p>
        <p>Please verify your account by clicking the button below:</p>
        <a th:href="${verificationLink}" class="btn">Verify Account</a>
    </div>
    <div class="footer">
        <p>© 2025 Library Management System. All rights reserved.</p>
        <p>If you didn’t request this email, please ignore it.</p>
    </div>
</div>
</body>
</html>
