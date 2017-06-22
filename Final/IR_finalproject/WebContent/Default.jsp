<%@ page language="java" pageEncoding="gb2312"%>
<%@ page contentType="text/html;charset= gb2312"%>
<!DOCTYPE html >
<html>
<head>
	<link href="./css/bootstrap.css" rel="stylesheet" type="text/css" />
<meta http-equiv="Content-Type" content="text/html charset=UTF-8">
<title>Etsy Search</title>
<style>
body{
	background-image: url("./images/etsy.jpg");
	background-size: 100%;
}
.etsy_form{
	margin-top:120px;
	margin-left:50px;
}
</style>
</head>
<body>
<div style="color: white; height: 20px;" class="container">
<h2>Etsy Enhanced Search Engine</h2>
</div>
<div class="etsy_form">
<form class="navbar-form navbar-left"name="sub_form" role="search" action="Query" method ="post" onsubmit="return validateForm()">
	<div class="form-group">
		<input type="text" name="SearchInput" class="form-control col-md-6" placeholder="Search">
	</div>
	<button type="submit" class="btn btn-default">Submit</button>
</form>
</div>
</body>
</html>
<script>
function validateForm() {
    var x = document.forms["sub_form"]["SearchInput"].value;
    x = x.trim();
    var nospecial=/[*|\":<>[\]{}`\\()';@&$%]/;
    if (nospecial.test(x) || x=="") {
        alert("请输入合理的中文：Please input valid keys");
        return false;
    }
}
</script>