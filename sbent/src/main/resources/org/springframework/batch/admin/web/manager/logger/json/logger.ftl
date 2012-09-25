<#import "/spring.ftl" as spring />
<#macro json_string string>${string?js_string?replace("\\'", "\'")?replace("\\>", ">")}</#macro>

"job" : {
  "id" : ${jobId},
  "log" : "<@json_string "${log}"/>"
}

