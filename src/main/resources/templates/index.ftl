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
                [#if isAdmin??]
                    <li><a href="/employee">Employees</a></li>
                    <li><a href="/project">Projects</a></li>
                    <li><a href="/cost">Costs</a></li>
                    <li><a href="/timecards">Timecards</a></li>
                    <li><a href="/logout">Logout</a>
                    </li>
      			[#elseif isUser??]
                    <li><a href="/timecards">Timecards</a></li>
                    <li><a href="/logout">Logout</a>
                [#else]
                     <li><a href="/login">Login</a></li>
                [/#if]
      		</ol>
</div>
</div>

</body>
</html>


