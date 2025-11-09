<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>Invespy</title>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Poppins:wght@100;200;300;400;500;600;700;800;900&display=swap');



        /* General resets */
        body {
          margin: 0;
          padding: 0;
          font-family: Poppins, Arial, sans-serif;
          background-color: #FFFFFF !important;
        }



        img {
          display: block;
          max-width: 100%;
          height: auto;
        }

        .content {
          max-width: 800px;
          margin: 0 auto;
          background: #F8FAFC;
        }

        .footer {
          color: #aaaaaa;
        }

        /* For Outlook */
        table,
        td {
          mso-table-lspace: 0pt;
          mso-table-rspace: 0pt;
        }

        img {
          -ms-interpolation-mode: bicubic;
        }





        @media (prefers-color-scheme: dark) {
          body {
            background-color: #FFFFFF;
            color: #F8FAFC;
          }

          .content {
            background: #121A26;
          }

          .footer {
            color: #aaaaaa;
          }


        }

        .learnMore {
          width: 160px;
          height: 50px;
        }


        .buttonText {
          font-size: 12px
        }
        .name{
          font-size: 18px
        }
        .heading{
          font-size: 24px;
        }
          .paragraph{
         font-size: 16px;
         }
      .button-play {

        border-radius: 100px;
        width: 220px; height: 32px;
      }
         .button-play img {
         border: 1px solid #fff;
         border-radius: 100px;
         padding: 1px;
         }



        @media screen and (max-width: 600px) {
          .content-footer .column {
            display: block;
            width: 100% !important;
            padding: 10px 0 !important;

          }

          .buttonText {
            font-size: 14px !important;
          }

          .hidden {
            display: none;
          }

          .learnMore {
            width: 150px !important;
            height: 40px !important;
          }
          .name{
          font-size: 12px !important;
          }
          .heading{
          font-size: 14px !important;
          }
          .paragraph{
           font-size: 12px !important;
           padding-block:6px !important;
         }

         .button-play {
         border-radius: 100px;
           width: 295px !important;
           height: 50px !important;
           padding: 0px !important;
        }




        }

        @media screen and (min-width: 600px) {
          .show {
            display: none;
          }

        }

         .button-container {
          text-align: left;
        }

        /* Gradient border workaround */
        .gradient-button {
          display: inline-flex;
          align-items: center;
          justify-content: center;
          padding: 2px 2px; /* Padding inside the button */
          background: linear-gradient(90deg, #E112DB, #2747ED); /* Outer gradient border */
          border-radius: 30px; /* Fully rounded corners */
          position: relative;
          text-decoration: none;
          overflow: hidden;
          width: auto;
          max-width: 100%;
        }

        /* Inner content with white background */
        .gradient-button span {
          display: inline-flex;
          align-items: center;
          justify-content: center;
          padding: 8px 18px; /* Inner padding */
          background: #ffffff; /* White background for the button */
          color: #000; /* Text color */
          font-size: 16px;
          font-weight: bold;
          border-radius: 25px; /* Matches the outer border rounding */
          width: 100%;
          height: 100%;
        }

        /* Arrow icon inside the button */
        .gradient-button img {
          margin-left: 10px;
          height: 20px;
          width: auto;
        }

        /* Hover effect */
        .gradient-button:hover span {
          background: #f5f5f5; /* Slightly darker inner background on hover */
        }

        /* Responsive adjustments */
        @media (max-width: 768px) {
          .gradient-button span {
            font-size: 10px;
            padding: 8px 14px;
          }
          .gradient-button img {
            height: 16px;
          }
        }

        @media (max-width: 480px) {
          .gradient-button span {
            font-size: 10px;
            padding: 4px 10px;
          }
        }
    </style>
</head>

<body>
<!-- Wrapper Table -->
<table role="presentation" width="100%" cellpadding="0" cellspacing="0"
       style="background-color: #F8FAFC; padding: 40px 20px;">
    <tr>
        <td align="center">
            <!-- Email Content Table -->
            <table class="content" role="presentation" width="100%" cellpadding="0" cellspacing="0">

                <!-- Header -->
                <tr>
                    <td align="center" style="padding: 20px 20px; padding-bottom: 80px; border-top-left-radius: 20px;border-top-right-radius: 20px;">
                        <table role="presentation" width="100%" cellpadding="0" cellspacing="0">
                            <tr>
                                <!-- First Image (single image) -->
                                <td align="left" width="50%" style="padding-right: 10px;">
                                    <a href="${weburl}">
                                        <img src="https://dkneumcx0kith.cloudfront.net/1744897935383-neutralgray%20logo.png"
                                             alt="" style="width: 119px; max-width: 200px; display: block;" />
                                    </a>
                                </td>
                                <!-- Second Div (four images) -->
                                <td align="right" width="15%" style="padding-left: 10px;opacity:0">
                                    <table role="presentation" width="100%" cellpadding="0" cellspacing="0">
                                        <tr>

                                        </tr>

                                    </table>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
