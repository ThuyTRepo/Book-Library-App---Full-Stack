# Linux - Install Development Tools

In this guide, we will install the following development tools

* Visual Studio Code
* nvm
* npm
* node
* tsc

## Versions

This course has been tested with the following software versions:

* nvm 0.35.0
* npm 8.15
* node 16.17.0
* tsc 4.7.4
* React 18.2.0
* Spring Boot 2.7.3 

It is **highly recommended** that you use the versions listed above to make sure you do not encounter any issues with the course. If you choose to use other versions then the code may not work as expected.

## Prerequisites
* git: You must have the git command-line tool installed. 

  * To install git, visit: https://git-scm.com/

## Install Visual Studio Code
Visual Studio Code is a general purpose IDE that support many programming languages. Visual Studio code has built-in support for TypeScript.


1. In a web browser, visit https://code.visualstudio.com/

2. Click the link to download Visual Studio Code for Linux

3. Follow the installation instructions for your Linux distribution.


## Install NVM
**nvm** is the [Node Version Manager](https://github.com/nvm-sh/nvm). It allows you to install multiple versions of Node on your computer. Once installed, then you can easily switch to a different version of Node. This is very useful if you want to develop/test using different versions of Node. When you are developing multiple projects, you will most likely need to support different versions of Node. 

The best feature of nvm is that it makes the installation of Node very easy for Mac and Linux system. Historically, to install Node on Mac/Linux, you would have to make frequent use of the "sudo" command to elevate privileges. This was a painful and clunky process during development. Thanks the nvm, we no longer have to use "sudo" when using Node.

Also, the Node ecosystem is a fast moving target and versions change frequently. nvm makes it easy to stay up to date with the latest version of Node. 

The website for nvm is: https://github.com/nvm-sh/nvm

> Note: You must have git installed before running this script.

### Install
1. Open a new terminal window

2. Enter the following commands:

    ```bash
    curl -o- https://raw.githubusercontent.com/creationix/nvm/v0.35.0/install.sh | bash
    ```

   The script clones the nvm repository to ~/.nvm and adds the source line to your profile (~/.bash_profile, ~/.zshrc, ~/.profile, or ~/.bashrc).

   You will see output similar to the following:

    ```
      % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                    Dload  Upload   Total   Spent    Left  Speed
    100 13527  100 13527    0     0  62065      0 --:--:-- --:--:-- --:--:-- 62336
    => Downloading nvm from git to '/home/luv2code/.nvm'
    => Cloning into '/home/luv2code/.nvm'...
    remote: Enumerating objects: 286, done.
    ...

    => Appending nvm source string to /home/luv2code/.bashrc
    => Appending bash_completion source string to /home/luv2code/.bashrc
    => Close and reopen your terminal to start using nvm or run the following to use it now:

    ...
    ```

3. Apply the new PATH settings with:

    ```bash
    source ~/.bashrc
    ```

4. Verify the installation, by typing the following command:

    ```bash
    nvm --version
    ```

   If the installation is successful, you will see the version number.

> For details on other nvm commands, use `nvm --help` or see the docs: https://github.com/nvm-sh/nvm


## Install Node
Node is the the runtime environment for executing JavaScript code from the command-line. By using Node, you can create any type of application using JavaScript including server-side / backend applications.

In this course, we'll use Node to run applications that we develop using TypeScript and Angular.

1. In your terminal window, type the following command:

    ```bash
    nvm install node
    ```

   You will see the following output

    ```
    Downloading and installing node v18.4.0...
    Downloading https://nodejs.org/dist/v18.4.0/node-v18.4.0-...
    ######################################################################## 100.0%
    Computing checksum with shasum -a 256
    Checksums matched!
    Now using node v18.4.0 (npm v8.12.1)
    Creating default alias: default -> node (-> v18.4.0)
    ```

2. Verify the node installation

    ```bash
    node --version
    ```

   If the installation is successful, you will see the version number

   _Note: The Node installation also includes npm (Node Package Manager)._

3. Verify npm is installed

    ```bash
    npm --version
    ```

   If the installation is successful, you will see the version number. 

   > Note: node will have a different number than npm. This is similar to a different Java JDK version number compared to Maven version number.
   >
   > In this example, node is similar to the Java JDK.  And npm is similar to Maven.

## Switch Node Versions

> Note: This course has been tested with Node 16.17. We will install this version.

1. Install and use Node 16.17

    ```bash
    nvm install 16.17.0
    nvm use 16.17.0
    ```

## Install tsc
tsc is the TypeScript compiler. We use tsc to compile TypeScript code into JavaScript code. We can install the TypeScript compiler using the Node Package Manager (npm)

> Note: This course has been tested with TypeScript 4.7. We will install this version.

1. In your terminal window, enter the following command

    ```
    npm install --location=global typescript@4.7.4
    ```

   The `--location=global` installs this as a global package. The TypeScript compiler will be available to all directories for this user.

2. You can verify the installation

    ```bash
    tsc --version
    ```

   If the installation is successful, you will see the version number.

That's it! You have successfully installed the development tools: Visual Studio Code, nvm, node, npm and tsc.
