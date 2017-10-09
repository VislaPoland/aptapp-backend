<!doctype html>
<html>
<head>
    <meta name="viewport" content="width=device-width">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Exception notification</title>
</head>

<body class="aptapp"
      style="margin: 0; font-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif; font-size: 14px; height: 100% !important; line-height: 1.6em; -webkit-font-smoothing: antialiased; padding: 0; -ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%; width: 100% !important; background-color: #f6f6f6;">

<p>
    Exception occurred at backend ${applicationUrl} at ${exceptionTime}
</p>
<p>&nbsp;</p>
<p>Exception message:<br>${exceptionMessage}</p>
<p>&nbsp;</p>
<p>Stack trace:</p>
<pre>${exceptionStackTrace}</pre>
</body>
</html>
