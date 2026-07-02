import os
import re
import xml.etree.ElementTree as ET
from pathlib import Path

# Configuration
SOURCE_DIR = Path("node_modules/@material-symbols/svg-500/rounded")
if not SOURCE_DIR.exists():
    SOURCE_DIR = Path("node_modules/@material-symbols/svg-500/rounded")

DEST_DIR = Path("shared/src/commonMain/composeResources/drawable")

# Mapping from your icon filename (without icon_ and _20px/_24px) to SVG filename in material-symbols
ICON_MAP = {
    "tooth": "dentistry",           # Use dentistry icon from material-symbols
    "account_circle_filled": "account_circle-fill",
    "arrow_back_24": "arrow_back",
    "bookmark_filled": "bookmark-fill",
    "explore_filled": "explore-fill",
    "home_filled": "home-fill",
    "image_24": "image",
    "notifications_active_24": "notifications_active",
    "notifications_filled": "notifications-fill",
    "poll": "bar_chart",
    "star_filled": "star-fill",
}

def svg_to_android_vector(svg_path, width_dp=24):
    with open(svg_path, 'r', encoding='utf-8') as f:
        svg_content = f.read()

    viewport_match = re.search(r'viewBox="([^"]+)"', svg_content)
    if not viewport_match:
        return None
    
    viewbox = viewport_match.group(1).split()
    vx = viewbox[0]
    vy = viewbox[1]
    vw = viewbox[2]
    vh = viewbox[3]

    path_match = re.search(r'<path\s+[^>]*d="([^"]+)"', svg_content)
    if not path_match:
        return None
    
    path_data = path_match.group(1)

    android_xml = f'''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="{width_dp}dp"
    android:height="{width_dp}dp"
    android:viewportWidth="{vw}"
    android:viewportHeight="{vh}"
    android:tint="?attr/colorControlNormal">
  <group android:translateY="{vh}">
    <path
        android:fillColor="#000000"
        android:pathData="{path_data}"/>
  </group>
</vector>'''
    return android_xml

def main():
    if not SOURCE_DIR.exists():
        print(f"Source directory {SOURCE_DIR} not found")
        return

    target_files = list(DEST_DIR.glob("icon_*_*.xml"))
    
    for target_file in target_files:
        filename = target_file.name
        match = re.match(r"icon_(.*)_(20px|24px|24)\.xml", filename)
        if not match:
            continue
        
        icon_name, size_str = match.groups()
        size_dp = 20 if size_str == "20px" else 24
        
        # Use mapping if exists, otherwise use icon_name as-is
        svg_base_name = ICON_MAP.get(icon_name, icon_name)
        svg_filename = f"{svg_base_name}.svg"
        svg_path = SOURCE_DIR / svg_filename
        
        if svg_path.exists():
            print(f"Converting {svg_filename} -> {filename}")
            android_xml = svg_to_android_vector(svg_path, size_dp)
            if android_xml:
                with open(target_file, 'w', encoding='utf-8') as f:
                    f.write(android_xml)
            else:
                print(f"Failed to parse {svg_filename}")
        else:
            print(f"SVG not found for {icon_name} (tried {svg_filename})")

if __name__ == "__main__":
    main()