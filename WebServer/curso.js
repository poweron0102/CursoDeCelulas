async function GetUserModulePontuation(curseName) {
    let response = await fetch('/GetUserModulePontuation', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            curseName: curseName,
            username: localStorage.getItem('LoginName')
        } )
    });
    response = await response.json();
    console.log(response);

    return response.modulePontuation;
}

let nomeCurso;
async function RemoveCurseFromUser() {
    let response = await fetch('/RemoveCurseFromUser', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            curseName: nomeCurso,
            username: localStorage.getItem('LoginName')
        } )
    });
    response = await response.json();
    console.log(response);
    window.location.href = '/curse/' + nomeCurso;
}

function GetGradeByName(gradeName, lista) {
    for (let grade of lista) {
        if (gradeName.trim() === decodeURIComponent(grade.name.trim())) {
            console.log("encontrado");
            return grade;
        }
        else {
            console.log(grade.name + " != " + gradeName);
        }
    }

    return -1;
}
function NameOfMinGrade(lista) {
    let min = 11;
    let name = '';
    for (let grade of lista) {
        if (grade.grade < min && grade.grade != -1) {
            min = grade.grade;
            name = grade.name;
        }
    }

    return name;
}


async function GetCurse(curseName) {
    nomeCurso = curseName;
    let response = await fetch('/getCurses', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({name: curseName} )
    });
    response = await response.json();
    console.log(response);

    mainDiv.innerHTML = '';

    mainDiv.innerHTML += response.curso;
    let curseMain = document.querySelector('.curse-main');
    curseMain.innerHTML += "<button onClick=\"RemoveCurseFromUser()\" id=\"ExitCurse\">Sair do cursor</button>";
    let moduleNames = response.modulos;
    let moduleUl = document.createElement('ul');
    moduleUl.id = 'moduleUl';
    console.log(moduleNames);
    let modulePontuation = await GetUserModulePontuation(curseName);
    for (let moduleName of moduleNames) {
        let moduleLi = document.createElement('li');
        moduleLi.className = 'moduleLi';
        moduleLi.innerHTML = moduleName;

        if (GetGradeByName(moduleName, modulePontuation) === -1) {
            moduleLi.onclick = function() {
                window.location.href = '/modulo/' + curseName + '/' + moduleName;
            }
        }
        else {
            moduleLi.innerHTML += '<br>(' + GetGradeByName(moduleName, modulePontuation).grade + ')';
        }

        moduleUl.appendChild(moduleLi);
    }

    let moduleLi = document.createElement('li');
    moduleLi.className = 'moduleLi';
    moduleLi.innerHTML = 'Revisão';

    moduleLi.onclick = function() {
        window.location.href = '/modulo/' + curseName + '/' + NameOfMinGrade(modulePontuation);
    }
    moduleUl.appendChild(moduleLi);

    mainDiv.appendChild(moduleUl);
}