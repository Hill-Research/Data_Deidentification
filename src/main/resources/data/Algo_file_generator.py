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
    res += "\n</Algo>\n"
    return (res)


def process_file(algoFile):
    print(algoFile)
    df = pd.read_csv(algoFile, header=None, delimiter=r"\s+")
    df4 = pd.DataFrame({'Code': df.iloc[:, 0], 'AlgoNumber': df.iloc[:, 1]})
    # print(to_xml(df4))
    output_file = "Output/" + algoFile.replace("_algo.txt", "_algo.xml")
    text_file = open(output_file, "w")
    xmlStr = "<Algo>\n" + to_xml(df4)
    text_file.write(xmlStr)
    text_file.close()


dirpath = Path('Output')
if dirpath.exists() and dirpath.is_dir():
    shutil.rmtree(dirpath)
os.mkdir("Output")

arr = os.listdir('./')
for file in arr:
    if "_algo.txt" in file:
        process_file(file)
