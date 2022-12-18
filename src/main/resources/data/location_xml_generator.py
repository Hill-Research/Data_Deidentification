#!/usr/bin/env python
import pandas as pd
import re
import os
from pathlib import Path
import shutil


def to_xml(df):
    def row_xml(row):
        xml = ['  <item>']
        for i, col_name in enumerate(row.index):
            xml.append('    <{0}>{1}</{0}>'.format(col_name, row.iloc[i]))
        xml.append('  </item>')
        return '\n'.join(xml)

    res = '\n'.join(df.apply(row_xml, axis=1))
    res += "\n</China_Area_Record>\n"
    return (res)


def process_file(locFile):
    df = pd.read_csv(locFile, header=None, delimiter=r"\s+")
    df4 = pd.DataFrame(
        {'level': df.iloc[:, 0], 'parent_code': df.iloc[:, 1], 'area_code': df.iloc[:, 2], 'name': df.iloc[:, 3],
         'merger_name': df.iloc[:, 4]})
    # print(to_xml(df4))
    output_file = "Output/location.xml"
    text_file = open(output_file, "w")
    xmlStr = "<China_Area_Record>\n" + to_xml(df4)
    text_file.write(xmlStr)
    text_file.close()


process_file('location.csv')
