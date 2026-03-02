import os
import re

directories_to_search = [
    r"c:\GaldusSDAI\SDAI-Sacchi\digita-twin-warehouse\backend\src\main\java\com\warehouse\digitaltwin"
]

files_with_log = []

for root, dirs, files in os.walk(directories_to_search[0]):
    for file in files:
        if file.endswith(".java"):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
                if 'log.' in content and 'Logger log =' not in content and 'Logger log=' not in content:
                    files_with_log.append(filepath)

for filepath in files_with_log:
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 1. Removelombok slf4j import
    content = re.sub(r'import lombok\.extern\.slf4j\.Slf4j;\n?', '', content)
    content = re.sub(r'@Slf4j\n?', '', content)
    
    # 2. Add logger imports if not exist
    if 'import org.slf4j.Logger;' not in content:
        # insert after the package declaration
        content = re.sub(r'(package .*?;)', r'\1\n\nimport org.slf4j.Logger;\nimport org.slf4j.LoggerFactory;', content, count=1)
        
    # 3. Add logger declaration inside the class
    class_name_match = re.search(r'public class (\w+)', content)
    if class_name_match:
        class_name = class_name_match.group(1)
        # Find the opening brace of the class
        class_def_pattern = r'(public class ' + class_name + r'.*?\{)'
        logger_decl = f'\n\n    private static final Logger log = LoggerFactory.getLogger({class_name}.class);'
        content = re.sub(class_def_pattern, r'\1' + logger_decl, content, count=1, flags=re.DOTALL)
        
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Fixed {filepath}")
