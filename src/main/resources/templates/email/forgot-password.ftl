<#include "layout/header.ftl">

<div style="display:none;font-size:1px;color:#fefefe;line-height:1px;max-height:0;max-width:0;opacity:0;overflow:hidden;">
    Here’s your link to reset your password and regain access to your account on Invespy
</div>
<!-- Body Content -->
<tr>
    <td
            style="padding: 20px 30px; text-align: left; color: #121A26; background-color: #FFFFFF; border-radius: 20px;">
        <p class="name" style=" color: #121A26; font-weight:600;">Hi <strong>${name},</strong></p>
        <p class="paragraph" style=" color: #121A26;">
            We received a request to reset the password for your Invespy account. If you made this request, please follow the instructions below to securely reset your password.

        </p>
        <p class="heading" style=" line-height: 1.5; font-weight: 600; color: #121A26; ">
            Reset Your Password:
        </p>

        <p class="paragraph" style=" color: #121A26;">
            Click the link below to create a new password: <a style="text-decoration: none; font-weight: 600;" href="${resetLink}">${resetLink}</a>

        </p>
        <p class="paragraph" style=" color: #121A26;">
            This link will be valid for the next 24 hours. For your security, please make sure not to share this link with anyone.
        </p>
        <p class="paragraph" style=" color: #121A26;">
            If you didn’t request a password reset, please ignore this email, and your account will remain secure.
        </p>
        <p class="paragraph" style=" color: #121A26;">
            For any questions or assistance, feel free to reach out to our support team at <a style="text-decoration: none; font-weight: 600;color: #121A26;" href="mailto:help@invespy.io">help@invespy.io</a>.
        </p>

        <!-- Verification Code Section (Replaces Flexbox) -->






<#include "layout/footer.ftl">
