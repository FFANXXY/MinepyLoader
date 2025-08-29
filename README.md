# Minepy Loader

**一个对于 Minepy 的 Minecraft Fabric模组**  
   
**A Minecraft Fabric Mod for Minepy**

## A.简体中文版

### 0.4.0-alpha.1-build+20250829 内部测试版

  1. 将`VarGetterNode`取代其它获取变量的方法，统一变量获得方法为`VarGetterNode`
  2. 方法调用时需要执行者内容，删除`protected`访问修饰符，改`private`修饰符为仅在同一个***包***内时才可调用
  3. 强制规范`ingame`修饰符必须在游戏运行时才可以执行，强制规范*只有*`load`修饰符才会在开始时被调用
  4. 添加 `for(int i = 0; i < x; i++)` 的`for`循环，修复`while语句`
  5. 在解析`[变量].[方法]()`时，允许调用变量类型的方法，实现为**将变量类型作为类，变量作为第一参数**的方法的快捷调用，例如`String a = "xxx"`原需要`String.charAt(a, <int>)`改为`a.charAt(<int>)`
  6. 允许*自加* *自减*，并支持 `+=` `-=` `*=` `/=` 等以`a ?= b`的计算作为 `a = b ? a`的解析
  7. 修复版本号，改`0.0.4-alpha` 为 `0.4.0-alpha.1`

### 1).关于该项目
  1. 该项目实现了对 `.mpy` <sub> (文本文件，为该项目的支持文件) </sub> 文件的代码解析，目的是使用项目内置定义的类似于Python编程语言、以及格式，快捷实现对Minecraft中游戏元素的添加，或者代替原版的指令、拓展原版指令逻辑、亦或扩展原版数据包的效果。
  2. 该项目将以可拓展性，语言便捷性和全面性为最终目标，实现以外置包，来达到与直接使用模组加载器一样的效果。

### 2).当前项目进度
  - 目前最新版本: 0.4.0-alpha.1-build+20250829 内部测试版 || 更新时间 2025.8.29 
  - __该项目仍处于内部测试版，可能仍旧拥有许多Bug，甚至可能对基本语法进行大改__，而 `Loader Version` 配置项暂时不会对内部测试版的内容进行支持。此配置项计划将于*正式版完成后*启用。
  - 完成的内容、半完成的内容、以及未完成的内容(* 内容将于下个版本完成):
    -----------------------------------
  - [x] 方法以及脚本解析
  - [x] 基本的变量定义
  - [x] 方法引用
  - [x] 指令支持
  - [ ] **面向对象**编程
  - [x] 逻辑代码
  - [ ] 资源文件与数据包支持
  - [ ] 外置包
  - [ ] !正式版!
### 3).语言文档  
   详见 https://github.com/FFANXXY/Minepy-Grammar/tree/main


## B. English Version

### 0.4.0-alpha.1-build+20250829 internal beta

**Sorry , the following words is translated by Translation software. And English is still not supported in the game.
        It is expected to be fixed in the official version `0.4`**

  1. Replace `VarGetterNode` with other methods to obtain variables, and unify the variable acquisition method as `VarGetterNode`
  2. When the method is called, the executor content is required, delete the `protected` access modifier, and change the `private` modifier to only be called when it is in the same package
  3. The mandatory `ingame` modifier must be executed while the game is running, and the mandatory *only the `load` modifier will be called at the start
  4. Add `for(int i = 0; i < x; i++)`, fix the 'while statement'
  5. In the parsing `[variable]. [Method] ()`, allowing the method of the variable type to be called, implemented as a quick call of the method with the variable type as the class and the variable as the first argument, for example, 'String a = "xxx"' originally required 'String.charAt(a, <int>)' to 'a.charAt(<int>)'
  6. Allow *self-addition* *self-subtraction*, and support `+=` `-=` `*=` `/=` etc. to use `a ?= b` as `a = b ? a`
  7. Fixed the version number, changed `0.0.4-alpha` to `0.4.0-alpha.1`

### 1). About the Project
  1. This project implements code parsing for `.mpy` <sub> (text files, the supported file format for this project) </sub> files. The goal is to use a project-internal definition of a language similar to the Python programming language in syntax and format, to quickly add game elements in Minecraft, replace vanilla commands, extend the logic of vanilla commands, or enhance the effects of vanilla data packs.
  2. With extensibility, language convenience, and comprehensiveness as its ultimate goals, this project aims to achieve effects comparable to using a mod loader directly, through external packages.

### 2). Current Project Progress
  - Current Latest Version: 0.4.0-alpha.1-build+20250829 Internal Beta || Last Updated 2025.8.29
  - __This project is still in its internal beta phase. There may still be many bugs, and even major changes to the basic syntax are possible.__ The `Loader Version` configuration item does not currently support internal beta content. This configuration item is planned to be enabled *after the official version is completed*.
  - Completed content, semi-completed content, and content not yet completed (* Content scheduled for completion in the next version):
    -----------------------------------
  - [x] Method and Script Parsing
  - [x] Basic Variable Definition
  - [x] Method Referencing
  - [x] Command Support
  - [ ] **Object-Oriented** Programming
  - [x] Control Flow Code
  - [ ] Resource File and Data Pack Support
  - [ ] External Packages
  - [ ] !Official Release!

### 3). Language Documentation
   For details, see https://github.com/FFANXXY/Minepy-Grammar/tree/main
