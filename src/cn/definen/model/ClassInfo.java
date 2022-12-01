package cn.definen.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassInfo {

    private String packageName;

    private String className;

    private Set<String> imports = new HashSet<>();

    /**
     * 备用
     */
    private String patchInfo;

    private List<FieldInfo> fields;


    public void addImport(String importPath) {
        imports.add(importPath);
    }
}
