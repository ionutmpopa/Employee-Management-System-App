[#ftl]

[#import "/spring.ftl" as spring /]
<html lang="en">
<head>
[#include '/bootstrap_header.ftl']
</head>
<body>

<div class="container">
    <a href="/"> <img src="[@spring.url '/images/logo.png' /]" width="100"/>
    </a>

        <ol class="breadcrumb">
        			<li class="active"><a href="/">Home</a></li>
                    <li><a href="/employee">Employees</a></li>
                                        <li><a href="/project">Projects</a></li>
                                        <li><a href="/cost">Costs</a></li>
                                        <li><a href="/timecards">Timecards</a></li>
                                        <li><a href="/logout">Logout</a>
        		</ol>




<div class="panel panel-default">
    <!-- Default panel contents -->
    <div class="panel-heading">Add project:
        <div style="float:right"><a href="employee/add"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></a></div>
    </div>


[#if errors??]
    [#list errors as error]
       <span style="color:red"> ${error}</span>
    <br>
    [/#list]
[/#if]
<form method="post" action="/project/save">

    Name: <input name="name" type="input" value="${project.name!''}">
    <br>
    Description: <input name="description" type="input"  value="${project.description!''}">

    [#if project.id??]
        <input name="id" type="hidden" value="${project.id?c}"/>
    [/#if]
    <input value="save" type="submit"/>
</form>

    </div>
</div>
</div>
[#include '/bootstrap_footer.ftl']
</body>
</html>