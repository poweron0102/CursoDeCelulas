let curseName;
let moduleName;
let numQuestions = 0;
let correctAnswers = 0;
let wrongAnswers = 0;
async function GetModule(curse, module) {
    curseName = curse;
    moduleName = module;
    let response = await fetch('/getModule', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({curseName: decodeURIComponent(curseName), moduleName: decodeURIComponent(moduleName)})
    });
    response = await response.json();
    console.log(response);

    mainDiv.innerHTML = '';
    mainDiv.innerHTML += response.module;

    numQuestions = response.questions.length;
    for (let question of response.questions) {
        mainDiv.innerHTML += question;
    }
}

async function SetUserModulePontuation(pontuation) {
    let response = await fetch('/SetUserModulePontuation', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            curseName: curseName,
            moduleName: moduleName,
            grade: pontuation,
            username: localStorage.getItem('LoginName')
        })
    });
    response = await response.json();
    console.log(response);
}

async function Answer(correct) {
    // Access the button that was clicked
    let button = event.target;

    // Access the parent div of the button
    let parentDiv = button.parentNode;

    // list all buttons on the parent div
    let buttons = parentDiv.getElementsByTagName('button');
    // if button `onclick="Answer(true)"` paint the button green else red
    for (let button of buttons) {
        if (button.onclick.toString().includes("true")) {
            button.style.backgroundColor = 'green';
        } else {
            button.style.backgroundColor = 'red';
        }
    }

    parentDiv = parentDiv.parentNode;

    // Make the parent div fade slowly
    parentDiv.style.transition = 'opacity 3s';
    parentDiv.style.opacity = "0";
    await new Promise(r => setTimeout(r, 3000));

    correctAnswers += correct ? 1 : 0;
    wrongAnswers += correct ? 0 : 1;

    // Remove the parent div
    parentDiv.remove();

    console.log("correctAnswers: " + correctAnswers + " wrongAnswers: " + wrongAnswers + " numQuestions: " + numQuestions);
    // if it is the last question, save the answers and redirect to the course page
    if (correctAnswers + wrongAnswers === numQuestions) {
        let pontuation = (correctAnswers / numQuestions) * 10;
        await SetUserModulePontuation(pontuation);
        window.location.href = '/curse/' + curseName;
    }
}