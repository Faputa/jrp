@echo on
set my=MyJrpService
net stop %my%
sc delete %my%
@echo 回车则进行安装，否则直接关闭窗口
pause

set p=%~dp0
set p=%p:\=/%
sc create %my% binPath= %p%%my%.exe
sc config %my% start= auto type= share
@echo 回车则进行服务启动，否则直接关闭窗口
pause

net start %my%
@echo 回车则关闭窗口
pause
