import os
import re

base_path = r"c:\GaldusSDAI\SDAI-Sacchi\digita-twin-warehouse\backend\src\main\java\com\warehouse\digitaltwin"

def to_camel_case(s):
    return s[0].upper() + s[1:]

def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # If it's not a class, skip
    if 'class ' not in content:
        return

    # Remove lombok imports & annotations
    content = re.sub(r'import lombok\..*?;\n?', '', content)
    content = re.sub(r'@Data\n?', '', content)
    content = re.sub(r'@Getter\n?', '', content)
    content = re.sub(r'@Setter\n?', '', content)
    content = re.sub(r'@Builder\n?', '', content)
    content = re.sub(r'@NoArgsConstructor\n?', '', content)
    content = re.sub(r'@AllArgsConstructor\n?', '', content)
    
    # Extract fields: private (Type) (name);
    # avoiding static, final, etc.
    fields = re.findall(r'private\s+((?:[\w<>,_ ]+))\s+(\w+)\s*;', content)
    
    methods_to_add = []
    for f_type, f_name in fields:
        f_type = f_type.strip()
        capitalized = to_camel_case(f_name)
        
        # Check getter
        getter_name = f'get{capitalized}()'
        if getter_name not in content and f'boolean {f_name}' not in f_type: # simplified boolean check
            methods_to_add.append(f"    public {f_type} get{capitalized}() {{ return {f_name}; }}")
        elif 'boolean' in f_type and f'is{capitalized}()' not in content:
            methods_to_add.append(f"    public {f_type} is{capitalized}() {{ return {f_name}; }}")
            
        # Check setter
        setter_name = f'set{capitalized}('
        if setter_name not in content:
            methods_to_add.append(f"    public void set{capitalized}({f_type} {f_name}) {{ this.{f_name} = {f_name}; }}")

    if methods_to_add:
        # insert before the last closing brace
        last_brace_idx = content.rfind('}')
        if last_brace_idx != -1:
            content = content[:last_brace_idx] + "\n".join(methods_to_add) + "\n" + content[last_brace_idx:]
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Added getters/setters to {os.path.basename(filepath)}")

for root, dirs, files in os.walk(base_path):
    for f in files:
        if f.endswith('.java'):
            process_file(os.path.join(root, f))
