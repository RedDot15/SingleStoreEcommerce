// MESSAGE INPUT
const textarea = document.querySelector('.chatbox-message-input')
const chatboxForm = document.querySelector('.chatbox-message-form')

textarea.addEventListener('input', function () {
    let line = textarea.value.split('\n').length

    if(textarea.rows < 6 || line < 6) {
        textarea.rows = line
    }

    if(textarea.rows > 1) {
        chatboxForm.style.alignItems = 'flex-end'
    } else {
        chatboxForm.style.alignItems = 'center'
    }
})



// TOGGLE CHATBOX
const chatboxToggle = document.querySelector('.chatbox-toggle')
const chatboxMessage = document.querySelector('.chatbox-message-wrapper')

chatboxToggle.addEventListener('click', function () {
    chatboxMessage.classList.toggle('show')
})



// DROPDOWN TOGGLE
const dropdownToggle = document.querySelector('.chatbox-message-dropdown-toggle')
const dropdownMenu = document.querySelector('.chatbox-message-dropdown-menu')

dropdownToggle.addEventListener('click', function () {
    dropdownMenu.classList.toggle('show')
})

document.addEventListener('click', function (e) {
    if(!e.target.matches('.chatbox-message-dropdown, .chatbox-message-dropdown *')) {
        dropdownMenu.classList.remove('show')
    }
})







// CHATBOX MESSAGE
const chatboxMessageWrapper = document.querySelector('.chatbox-message-content')
const chatboxNoMessage = document.querySelector('.chatbox-message-no-message')

chatboxForm.addEventListener('submit', function (e) {
    e.preventDefault()

    if(isValid(textarea.value)) {
        writeMessage()
        setTimeout(autoReply, 1000)
    }
})



function addZero(num) {
    return num < 10 ? '0'+num : num
}

let save = '';

function writeMessage() {
    const today = new Date()
    let message = `
		<div class="chatbox-message-item sent">
			<span class="chatbox-message-item-text">
				${textarea.value.trim().replace(/\n/g, '<br>\n')}
			</span>
			<span class="chatbox-message-item-time">${addZero(today.getHours())}:${addZero(today.getMinutes())}</span>
		</div>
	`
    chatboxMessageWrapper.insertAdjacentHTML('beforeend', message)
    chatboxForm.style.alignItems = 'center'
    save = textarea.value.trim();
    textarea.rows = 1
    textarea.focus()
    textarea.value = ''
    chatboxNoMessage.style.display = 'none'
    scrollBottom()
}

async function autoReply() {
    const today = new Date();

    const API_URL = "https://a6a5-35-222-53-126.ngrok-free.app/routegicungduoc";
    const data = {
        "message" : save
    };
    const requestOptions = {
        method: "POST",
        headers:{
            "Content-Type": "application/json",
        },
        body: JSON.stringify(data),

    };

    let message;

    fetch(API_URL, requestOptions)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            console.log(data);
            message = `
                <div class="chatbox-message-item received">
                    <span class="chatbox-message-item-text">
                        ${data.answer}
                    </span>
                    <span class="chatbox-message-item-time">${addZero(today.getHours())}:${addZero(today.getMinutes())}</span>
                </div>
            `;
            chatboxMessageWrapper.insertAdjacentHTML('beforeend', message)
        })
        .catch(error => {
            console.error('Error:', error);
        }).finally(() => scrollBottom());

    message = `
        <p class="chatbox-message-no-message" style="font-size: 12px">Thinking... This may take a while :D</p>
    `;
    chatboxMessageWrapper.insertAdjacentHTML('beforeend', message);
    scrollBottom()
}

function scrollBottom() {
    chatboxMessageWrapper.scrollTo(0, chatboxMessageWrapper.scrollHeight)
}

function isValid(value) {
    let text = value.replace(/\n/g, '')
    text = text.replace(/\s/g, '')

    return text.length > 0
}