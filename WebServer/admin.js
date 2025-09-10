let fileUpload;
let adminSendBtn;
let userListUl;
let userTemplate;
function loadVariables() {
    fileUpload = document.getElementById('fileUpload');
    adminSendBtn = document.getElementById('admin-send');
    userListUl = document.getElementById('user-list-ul');
    userTemplate = document.getElementById('user-template');
    console.log(userTemplate);
}


async function LoadAdmin() {
    let adminPage = await (await fetch('/admin.html')).text();
    mainDiv.innerHTML += adminPage;
    loadVariables();

    // get users from "/getUsers"
    let users = await fetch('/getUsers' , {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({})
    });
    users = await users.json();
    console.log(users.users);

    // add users to userListUl following the userTemplate
    for (let user of users.users) {
        if (user.username === localStorage.getItem('LoginName')) {
            continue;
        }
        let userLi = userTemplate.content.cloneNode(true);
        userLi.querySelector('p').innerText = user.username;

        let removeUserBtn = userLi.querySelector('.remove-user');
        removeUserBtn.onclick = async function() {
            let response = await fetch('/removeUser', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({username: user.username})
            });
            response = await response.json();
            console.log(response);
            if (response.success) {
                location.reload();
            }
        }

        let demoteUserBtn = userLi.querySelector('.demote-user');
        let promoteUserBtn = userLi.querySelector('.promote-user');
        if (!user.isAdmin) {
            demoteUserBtn.remove();
            promoteUserBtn.onclick = async function () {
                let response = await fetch('/promoteUser', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({username: user.username})
                });
                response = await response.json();
                console.log(response);
                if (response.success) {
                    location.reload();
                }
            }
        }
        else {
            promoteUserBtn.remove();
            demoteUserBtn.onclick = async function () {
                let response = await fetch('/demoteUser', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({username: user.username})
                });
                response = await response.json();
                console.log(response);
                if (response.success) {
                    location.reload();
                }
            }
        }

        userListUl.appendChild(userLi);
    }

    // add an event listener to fileUpload to upload a file as string
    adminSendBtn.onclick = async function() {
        let file = fileUpload.files[0];
        let reader = new FileReader();
        reader.onload = async function() {
            let response = await fetch('/addCourse', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    fileContent: reader.result,
                    fileName: file.name
                })
            });
            response = await response.json();
            console.log(response);
        }
        reader.readAsText(file);
    }
}