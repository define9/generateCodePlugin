<#list fields as field>
    @Test
    void get${field.name?cap_first}() {
    ${className} ${className?uncap_first} = new ${className}();
    ${field.patchInfo!}
    ${className?uncap_first}.set${field.name?cap_first}(${field.demo});
    assertEquals(${field.demo},${className?uncap_first}.get${field.name?cap_first}());
    }

</#list>