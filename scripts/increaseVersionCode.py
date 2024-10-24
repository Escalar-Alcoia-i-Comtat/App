import os
import os.path as path

root = path.join(path.dirname(__file__), '..')
versionFile = path.join(root, "version.properties")

if not path.exists(versionFile):
    print(f"Version file ({versionFile}) doesn't exist.")
    exit(1)

new_lines = None

# Read the file line by line, and replace the version code with the next one
with open(versionFile) as f:
    lines = f.readlines()
    for i in range(len(lines)):
        line = lines[i]
        if line.startswith("VERSION_ANDROID_CODE="):
            # Get the number after the prefix
            code_str = line.split("VERSION_ANDROID_CODE=", 1)[1]
            # Increase by 1
            code = int(code_str)+1
            print(f"Increasing version code to {code}")
            # Replace that line
            lines[i] = f"VERSION_ANDROID_CODE={code}\n"
    new_lines = lines

if new_lines is None:
    print("Version file didn't contain an VERSION_ANDROID_CODE parameter.")
    print("The value will be initialized and set to 1")
    with open(versionFile, "a") as f:
        f.write("VERSION_ANDROID_CODE=1\n")
else:
    os.remove(versionFile)
    with open(versionFile, "w") as f:
        f.writelines(new_lines)