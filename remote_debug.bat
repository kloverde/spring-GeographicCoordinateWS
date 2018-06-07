@echo off

REM RealtimeMonitor
REM https://github.com/kloverde/spring-GeographicCoordinateWS
REM
REM Copyright (c) 2018, Kurtis LoVerde
REM All rights reserved.
REM
REM Donations:  https://paypal.me/KurtisLoVerde/10
REM
REM Redistribution and use in source and binary forms, with or without
REM modification, are permitted provided that the following conditions are met:
REM
REM     1. This software may not be used by any for-profit entity, whether a
REM        business, person, or other, or for any for-profit purpose.
REM     2. Redistributions of source code must retain the above copyright
REM        notice, this list of conditions and the following disclaimer.
REM     3. Redistributions in binary form must reproduce the above copyright
REM        notice, this list of conditions and the following disclaimer in the
REM        documentation and/or other materials provided with the distribution.
REM     4. Neither the name of the copyright holder nor the names of its
REM        contributors may be used to endorse or promote products derived from
REM        this software without specific prior written permission.
REM
REM THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
REM ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
REM WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
REM DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
REM FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
REM DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
REM SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
REM CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
REM OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
REM OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

pushd "%~dp0"

java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=4000,suspend=n -jar build\libs\GeographicCoordinateWS-0.0.0.jar

popd
