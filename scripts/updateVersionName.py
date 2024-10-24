import argparse
import os
import os.path as path
import plistlib
import re

root = path.join(path.dirname(__file__), '..')
versionFile = path.join(root, "version.properties")

if not path.exists(versionFile):
    print(f"Version file ({versionFile}) doesn't exist.")
    exit(1)

parser = argparse.ArgumentParser(description="Update Version Name")
parser.add_argument('--name', action="store", dest='name', default=None)
args = parser.parse_args()
version_name: str|None = args.name

if version_name is None:
    print("Version name not specified.")
    print("Usage: python3 updateVersionName.py --name=1.0.0")
    exit(1)

# Verify that the version name is valid
version_pieces = version_name.split(".")
if len(version_pieces) > 3 or len(version_pieces) < 1:
    print("Version name should be composed of between 1 and 3 integers separated by '.'")
    print("Examples: 1.0.0 1.0")
    exit(1)

for piece in version_pieces:
    if not piece.isdigit():
        print("Version name should be composed of between 1 and 3 integers separated by '.'")
        print("Examples: 1.0.0 1.0")
        exit(1)

print(f"Updating Version to: {version_name}")

new_lines = None

with open(versionFile) as f:
    lines = f.readlines()
    for i in range(len(lines)):
        line = lines[i]
        if line.startswith("VERSION_NAME="):
            lines[i] = f"VERSION_NAME={version_name}\n"
    new_lines = lines

os.remove(versionFile)
with open(versionFile, "w") as f:
    f.writelines(new_lines)

def replace_plist_property(plist_path, property_name, new_value):
    """
    Replaces the specified property in the given plist file with the new value.

    Args:
        plist_path (str): The path to the plist file.
        property_name (str): The name of the property to replace.
        new_value (str): The new value for the property.
    """

    try:
        # Load the plist file
        with open(plist_path, 'rb') as f:
            plist_data = plistlib.load(f)

        # Find and replace the property
        plist_data[property_name] = new_value

        # Write the modified plist back to the file
        with open(plist_path, 'wb') as f:
            plistlib.dump(plist_data, f)

        print(f"Successfully replaced property '{property_name}' in {plist_path}")
    except Exception as e:
        print(f"Error replacing property: {e}")

def replace_marketing_version(file_path, version_name):
    """Replaces the MARKETING_VERSION property in an iOS project with the given version name.

    Args:
        file_path: The path to the project file.
        version_name: The new version name to set.
    """

    lines = []
    with open(file_path, "r") as f:
        lines = f.readlines()
        for i in range(len(lines)):
            line = lines[i]
            if "MARKETING_VERSION" in line:
                lines[i] = re.sub('MARKETING_VERSION ?= ?\\d+\\.\\d+\\.\\d+', f"MARKETING_VERSION = {version_name}", line)
    os.remove(file_path)
    with open(file_path, "w") as f:
        f.writelines(lines)

plist_file = path.join(root, "iosApp", "iosApp", "Info.plist")
pbxproj_file = path.join(root, "iosApp", "iosApp.xcodeproj", "project.pbxproj")

replace_plist_property(plist_file, "CFBundleShortVersionString", version_name)
replace_marketing_version(pbxproj_file, version_name)