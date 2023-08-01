#!/usr/bin/env python

#  This program is free software; you can redistribute it and/or
#  modify it under the terms of the GNU General Public License
#  as published by the Free Software Foundation; either version 3
#  of the License, or (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program; if not, write to the Free Software
#  Foundation, Inc., 51 Franklin Street, Fifth Floor,
#  Boston, MA  02110-1301, USA.

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
