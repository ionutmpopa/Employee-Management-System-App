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
    			<li class="active">Home</li>
                <li><a href="/employee">Employees</a></li>
            	<li><a href="/project">Projects</a></li>
            	<li><a href="/cost">Costs</a></li>
            	<li><a href="/timecards">Timecards</a></li>
    		</ol>


    		<h1>
            Something went wrong ... sorry. <br/>

            </h1>
             [#if applicationError??]
            ${applicationError.code} - ${applicationError.message}
            [/#if]
</div>
</div>

</body>
</html>


