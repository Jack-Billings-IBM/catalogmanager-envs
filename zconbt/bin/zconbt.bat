@echo off
@REM
@REM HZC3000 - z/OS Connect Enterprise Edition 3.0.41
@REM
@REM <copyright
@REM notice="rm-source-program"
@REM pids="5655-CE3"
@REM years="2016,2017" >
@REM Licensed Materials - Property of IBM
@REM
@REM Restricted Materials of IBM
@REM
@REM 5655-CE3
@REM
@REM (C) Copyright IBM Corp. 2016, 2017 
@REM
@REM US Government Users Restricted Rights - Use, duplication or
@REM disclosure restricted by GSA ADP Schedule Contract with
@REM IBM Corp.
@REM </copyright>

setlocal enabledelayedexpansion

set CURRENT_DIR="%~dp0"
set CURRENT_DIR=!CURRENT_DIR:"=!
set INVOKED="%~0"
set INVOKED=!INVOKED:"=!
set PARAMS_QUOTED=%*
if "zccz"=="zc%JAVA_OPTIONS%cz" set JAVA_OPTIONS=

java %JAVA_OPTIONS% -classpath "%CURRENT_DIR%\..\lib\*;%CURRENT_DIR%\..\plugins\*" com.ibm.zosconnect.buildtoolkit.cli.BuildToolkit %PARAMS_QUOTED%