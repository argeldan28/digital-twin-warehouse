import os
import re

base_path = r"c:\GaldusSDAI\SDAI-Sacchi\digita-twin-warehouse\backend\src\main\java\com\warehouse\digitaltwin"

# 1. Create TaskType enum if missing
task_type_dir = os.path.join(base_path, "domain", "model")
task_type_file = os.path.join(task_type_dir, "TaskType.java")
if not os.path.exists(task_type_file):
    with open(task_type_file, 'w', encoding='utf-8') as f:
        f.write('''package com.warehouse.digitaltwin.domain.model;

public enum TaskType {
    PICKUP,
    DROPOFF,
    MOVE,
    CHARGE
}
''')
    print("Created TaskType.java")

# 2. Fix SimulationEngine constructor (it had @RequiredArgsConstructor likely)
se_file = os.path.join(base_path, "engine", "SimulationEngine.java")
with open(se_file, 'r', encoding='utf-8') as f:
    se_content = f.read()

if 'public SimulationEngine(Warehouse' not in se_content:
    # Add the constructor based on private final fields
    pattern = r'private final (\w+)(?:<.*?>)? (\w+);'
    fields = re.findall(pattern, se_content)
    # Filter out static or non-injected things if any, but let's just grab all final fields
    args = []
    assignments = []
    for t, n in fields:
        if n != 'log': # skip logger
            args.append(f"{t} {n}")
            assignments.append(f"        this.{n} = {n};")
    
    constructor_code = f"\n    public SimulationEngine({', '.join(args)}) {{\n" + "\n".join(assignments) + "\n    }\n"
    # insert after the last private final field declaration
    last_field_match = list(re.finditer(pattern, se_content))[-1]
    insertion_idx = last_field_match.end()
    se_content = se_content[:insertion_idx] + "\n" + constructor_code + se_content[insertion_idx:]
    
    with open(se_file, 'w', encoding='utf-8') as f:
        f.write(se_content)
    print("Fixed SimulationEngine constructor")

print("Done phase 1 fixes.")
