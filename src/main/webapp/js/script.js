// Esperamos a que todo el HTML esté completamente cargado antes de ejecutar el código
document.addEventListener('DOMContentLoaded', () => {

    // Ruta base para las peticiones al Servidor Java (en local suele estar vacía si está en la misma carpeta)
    const API_BASE_URL = '';

    // ==========================================
    // 1. LÓGICA DEL MENÚ LATERAL (SIDEBAR)
    // ==========================================
    const sidebarElement = document.getElementById('sidebar-menu');
    const toggleSidebarBtn = document.getElementById('toggle-sidebar');

    if (sidebarElement && toggleSidebarBtn) {
        toggleSidebarBtn.addEventListener('click', () => {
            // Comprobamos si el menú tiene la clase 'collapsed' (oculto)
            const isCollapsed = sidebarElement.classList.contains('collapsed');
            if (isCollapsed) {
                // Si está oculto, lo mostramos cambiando las clases CSS
                sidebarElement.classList.remove('collapsed');
                sidebarElement.classList.add('expanded');
                toggleSidebarBtn.setAttribute('aria-expanded', 'true');
            } else {
                // Si está visible, lo ocultamos
                sidebarElement.classList.remove('expanded');
                sidebarElement.classList.add('collapsed');
                toggleSidebarBtn.setAttribute('aria-expanded', 'false');
            }
        });
    }

    // ==========================================
    // 2. ELEMENTOS DEL MAPA Y VARIABLES DE ESTADO
    // ==========================================
    const mapAsset = document.getElementById('map-asset');
    const mapViewport = document.querySelector('.map-viewport');
    const mapWrapper = document.getElementById('map-wrapper');
    const markersLayer = document.getElementById('markers-layer');
    const markerModal = document.getElementById('marker-modal');
    const closeMarkerModalBtn = document.getElementById('close-marker-modal');
    const markerForm = document.getElementById('marker-form');
    const markerNameInput = document.getElementById('marker-name-input');
    const markerCategoryInput = document.getElementById('marker-category-input');

    // Objeto para llevar la cuenta de cuántos marcadores hay de cada tipo
    const trackerCounts = {
        tesoro: { total: 0, marked: 0 },
        mision: { total: 0, marked: 0 },
        santuario: { total: 0, marked: 0 },
        poi: { total: 0, marked: 0 }
    };

    // Nombres legibles para mostrar en el menú lateral
    const trackerNames = {
        tesoro: 'Tesoros',
        mision: 'Misiones',
        santuario: 'Santuarios',
        poi: 'Puntos de Interés'
    };

    // Función para actualizar los contadores en el menú lateral de HTML
    const actualizarContadorUI = (category) => {
        const titleElement = document.getElementById(`title-${category}`);
        if (titleElement && trackerCounts[category]) {
            titleElement.textContent = `${trackerNames[category]} ${trackerCounts[category].marked}/${trackerCounts[category].total}`;
        }
    };

    // Función principal para DIBUJAR un marcador en la pantalla (recibe datos de LeerMarcadoresController)
    const dibujarMarcador = (id, nombre, categoria, x, y, estaTachado = false) => {
        // Creamos un div dinámicamente con JavaScript
        const elementoMarcador = document.createElement('div');
        elementoMarcador.className = `map-marker ${categoria}`;
        if (estaTachado) {
            elementoMarcador.classList.add('tachado');
        }
        // Lo posicionamos de forma absoluta usando las coordenadas de la base de datos
        elementoMarcador.style.left = `${x}px`;
        elementoMarcador.style.top = `${y}px`;
        elementoMarcador.title = `${nombre} (${categoria})`;
        elementoMarcador.dataset.id = id;
        elementoMarcador.dataset.category = categoria;

        // Sumamos 1 al contador visual
        if (trackerCounts[categoria]) {
            trackerCounts[categoria].total++;
            if (estaTachado) {
                trackerCounts[categoria].marked++;
            }
            actualizarContadorUI(categoria);
        }

        // Gestiona el clic en el marcador: llama a BorrarMarcadorController (admin) o TacharMarcadorController (usuario)
        const accionAlClickarMarcador = (evento) => {
            evento.preventDefault(); // Evitamos el menú nativo

            const rolUsuario = localStorage.getItem('user_rol');

            // LÓGICA PARA EL ADMIN → envía datos a BorrarMarcadorController
            if (rolUsuario === 'admin') {
                const confirmarBorrado = confirm(`¿Quieres eliminar el marcador "${nombre}"?`);
                if (confirmarBorrado) {
                    const datosBorrar = { idMarcador: parseInt(id) };
                    fetch(`${API_BASE_URL}/BorrarMarcadorController`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(datosBorrar)
                    })
                        .then(respuesta => {
                            if (!respuesta.ok) throw new Error('Error al borrar');
                            elementoMarcador.remove();
                            if (trackerCounts[categoria]) {
                                trackerCounts[categoria].total--;
                                if (elementoMarcador.classList.contains('tachado')) {
                                    trackerCounts[categoria].marked--;
                                }
                                actualizarContadorUI(categoria);
                            }
                        })
                        .catch(error => alert('No se pudo borrar el marcador.'));
                }
            }
            // LÓGICA PARA USUARIOS NORMALES → envía datos a TacharMarcadorController
            else if (rolUsuario === 'usuario') {
                // Comprobamos si ya tiene la clase 'tachado' para hacer lo contrario (toggle)
                const marcadorTachado = elementoMarcador.classList.contains('tachado');
                const nuevoEstado = !marcadorTachado;

                const datosTachar = {
                    idMarcador: parseInt(id),
                    tachado: nuevoEstado
                };

                fetch(`${API_BASE_URL}/TacharMarcadorController`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(datosTachar)
                })
                    .then(respuesta => {
                        if (!respuesta.ok) throw new Error('Error al actualizar');
                        // Si Java da el OK, le ponemos o quitamos la clase gris visualmente
                        if (nuevoEstado) {
                            elementoMarcador.classList.add('tachado');
                            if (trackerCounts[categoria]) {
                                trackerCounts[categoria].marked++;
                                actualizarContadorUI(categoria);
                            }
                        } else {
                            elementoMarcador.classList.remove('tachado');
                            if (trackerCounts[categoria]) {
                                trackerCounts[categoria].marked--;
                                actualizarContadorUI(categoria);
                            }
                        }
                    })
                    .catch(error => alert('No se pudo marcar como visto.'));
            } else {
                // Si es un invitado sin iniciar sesión
                alert('Inicia sesión para poder marcar los puntos que has visitado.');
            }
        };

        // Evento: Al hacer CLICK DERECHO (contextmenu) o CLICK/TOCAR normal
        elementoMarcador.addEventListener('contextmenu', accionAlClickarMarcador);
        elementoMarcador.addEventListener('click', accionAlClickarMarcador);

        // Añadimos el marcador recién creado a la capa del mapa
        markersLayer.appendChild(elementoMarcador);
    };

    // Comprobamos que estamos en la página del mapa antes de ejecutar la lógica
    if (mapAsset && mapViewport && mapWrapper && markersLayer && markerModal && closeMarkerModalBtn && markerForm && markerNameInput && markerCategoryInput) {

        // Variables matemáticas para el arrastre y el zoom del mapa
        let isDragging = false;
        let startX = 0; let startY = 0;
        let translateX = 0; let translateY = 0;
        let scale = 1;
        let minScale = 0.1;
        let tempMarkerX = 0; let tempMarkerY = 0;

        const actualizarTransformacionMapa = () => {
            mapWrapper.style.transform = `translate(${translateX}px, ${translateY}px) scale(${scale})`;
            mapWrapper.style.setProperty('--marker-scale', 1 / scale);
        };

        const centrarMapa = () => {
            const viewportRect = mapViewport.getBoundingClientRect();
            const mapWidth = mapAsset.naturalWidth || mapWrapper.offsetWidth;
            const mapHeight = mapAsset.naturalHeight || mapWrapper.offsetHeight;

            if (mapWidth > 0 && mapHeight > 0) {
                // Calcular escala para encajar el mapa
                const scaleX = viewportRect.width / mapWidth;
                const scaleY = viewportRect.height / mapHeight;
                minScale = Math.min(scaleX, scaleY, 1);
                scale = minScale; // Inicia con el mapa encajado

                translateX = (viewportRect.width - (mapWidth * scale)) / 2;
                translateY = (viewportRect.height - (mapHeight * scale)) / 2;

                actualizarTransformacionMapa();
            }
        };

        if (mapAsset.complete) {
            centrarMapa();
        } else {
            mapAsset.addEventListener('load', centrarMapa);
        }

        // ==========================================
        // 3. READ (CRUD): CARGAR MARCADORES DE MYSQL
        // ==========================================
        // Petición GET asíncrona mediante promesa (fetch) al Servlet de Java
        // Petición GET a LeerMarcadoresController para obtener todos los marcadores
        fetch(`${API_BASE_URL}/LeerMarcadoresController`)
            .then(respuesta => {
                if (!respuesta.ok) throw new Error('Error al cargar marcadores');
                return respuesta.json(); // Transformamos la respuesta de texto plano a formato JSON
            })
            .then(datos => {
                // Si la base de datos devuelve un array, lo recorremos y dibujamos punto por punto
                if (Array.isArray(datos)) {
                    datos.forEach(m => {
                        dibujarMarcador(m.idMarcador, m.nombre, m.categoria, m.coordX, m.coordY, m.tachado);
                    });
                }
            })
            .catch(error => console.error('Error de conexión:', error));

        // ==========================================
        // 4. LÓGICA DE NAVEGACIÓN POR EL MAPA
        // ==========================================

        // Evitamos que la imagen se arrastre nativamente (el bug del "fantasma" de la imagen)
        mapAsset.addEventListener('dragstart', (e) => e.preventDefault());

        // Al hacer clic o tocar, empezamos a arrastrar
        mapViewport.addEventListener('mousedown', (e) => {
            if (e.target.classList.contains('map-marker')) return; // Si hace clic en un marcador, no arrastramos
            if (markerModal.contains(e.target)) return;
            isDragging = true;
            startX = e.clientX - translateX;
            startY = e.clientY - translateY;
            mapWrapper.style.cursor = 'grabbing';
        });
        mapViewport.addEventListener('touchstart', (e) => {
            if (e.target.classList.contains('map-marker')) return;
            if (markerModal.contains(e.target)) return;
            if (e.touches.length === 1) {
                isDragging = true;
                startX = e.touches[0].clientX - translateX;
                startY = e.touches[0].clientY - translateY;
                mapWrapper.style.cursor = 'grabbing';
            }
        }, { passive: false });

        // Al soltar el clic, paramos el arrastre
        window.addEventListener('mouseup', () => {
            isDragging = false;
            if (mapWrapper) mapWrapper.style.cursor = 'grab';
        });
        window.addEventListener('touchend', () => {
            isDragging = false;
            if (mapWrapper) mapWrapper.style.cursor = 'grab';
        });

        // Al mover el ratón o el dedo, calculamos la traslación si se está arrastrando
        mapViewport.addEventListener('mousemove', (e) => {
            if (!isDragging) return;
            translateX = e.clientX - startX;
            translateY = e.clientY - startY;
            actualizarTransformacionMapa();
        });
        mapViewport.addEventListener('touchmove', (e) => {
            if (!isDragging || e.touches.length !== 1) return;
            // Prevent scroll on touch devices when dragging the map
            if (e.cancelable) e.preventDefault();
            translateX = e.touches[0].clientX - startX;
            translateY = e.touches[0].clientY - startY;
            actualizarTransformacionMapa();
        }, { passive: false });

        // Al usar la rueda del ratón, calculamos el Zoom matemático hacia el puntero
        mapViewport.addEventListener('wheel', (e) => {
            e.preventDefault();

            const viewportRect = mapViewport.getBoundingClientRect();
            const mouseX = e.clientX - viewportRect.left;
            const mouseY = e.clientY - viewportRect.top;
            const zoomSpeed = 0.1;
            let siguienteEscala;

            if (e.deltaY < 0) {
                siguienteEscala = Math.min(scale * (1 + zoomSpeed), 8); // Zoom in máximo (x8)
            } else {
                siguienteEscala = Math.max(scale * (1 - zoomSpeed), minScale); // Zoom out mínimo (minScale)
            }

            if (siguienteEscala !== scale) {
                const mapX = (mouseX - translateX) / scale;
                const mapY = (mouseY - translateY) / scale;

                scale = siguienteEscala;
                translateX = mouseX - mapX * scale;
                translateY = mouseY - mapY * scale;

                actualizarTransformacionMapa();
            }
        }, { passive: false });

        // ==========================================
        // 5. CREATE (CRUD): AÑADIR MARCADOR
        // ==========================================

        // Al hacer doble clic en el mapa, abrimos el menú para crear el marcador
        mapViewport.addEventListener('dblclick', (e) => {
            if (e.target.classList.contains('map-marker')) return;
            if (markerModal.contains(e.target)) return;

            // Validación: Solo el rol 'admin' puede llegar a ver la ventana emergente
            const userRol = localStorage.getItem('user_rol');
            if (userRol !== 'admin') {
                alert('Solo los administradores pueden añadir puntos al mapa.');
                return;
            }

            // Calculamos en qué punto exacto matemático del plano se ha hecho clic respecto al zoom
            const viewportRect = mapViewport.getBoundingClientRect();
            const mouseX = e.clientX - viewportRect.left;
            const mouseY = e.clientY - viewportRect.top;

            tempMarkerX = (mouseX - translateX) / scale;
            tempMarkerY = (mouseY - translateY) / scale;

            // Limpiamos los inputs y mostramos la ventana modal
            markerNameInput.value = '';
            markerCategoryInput.value = '';
            markerModal.classList.remove('hidden');
            setTimeout(() => markerNameInput.focus(), 50);
        });

        // Botones para cerrar la modal pulsando la X o fuera de ella
        closeMarkerModalBtn.addEventListener('click', () => markerModal.classList.add('hidden'));
        markerModal.addEventListener('click', (e) => {
            if (e.target === markerModal) markerModal.classList.add('hidden');
        });

        // Cuando el usuario le da al botón "AÑADIR AL MAPA" → envía datos a CrearMarcadorController
        markerForm.addEventListener('submit', (e) => {
            e.preventDefault(); // Evitamos que la página se recargue

            const nombreMarcador = markerNameInput.value.trim();
            const categoriaMarcador = markerCategoryInput.value;
            if (nombreMarcador === '' || categoriaMarcador === '') return;

            // Creamos un objeto con los datos a enviar a CrearMarcadorController
            const datosNuevoMarcador = {
                nombre: nombreMarcador,
                categoria: categoriaMarcador,
                coordX: parseFloat(tempMarkerX.toFixed(2)),
                coordY: parseFloat(tempMarkerY.toFixed(2))
            };

            // Petición POST a CrearMarcadorController
            fetch(`${API_BASE_URL}/CrearMarcadorController`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(datosNuevoMarcador)
            })
                .then(respuesta => {
                    if (!respuesta.ok) throw new Error('Error en el servidor');
                    return respuesta.json();
                })
                .then(datos => {
                    // Si Java dice que OK, lo dibujamos gráficamente (Date.now() es un ID temporal)
                    dibujarMarcador(Date.now(), nombreMarcador, categoriaMarcador, tempMarkerX, tempMarkerY);
                    markerModal.classList.add('hidden'); // Ocultamos la ventana
                })
                .catch(error => {
                    alert('No se pudo guardar el marcador en la base de datos.');
                });
        });
    }

    // ==========================================
    // 6. FORMULARIOS DE AUTENTICACIÓN
    // ==========================================

    // Lógica para el formulario de LOGIN
    const formularioLogin = document.getElementById('login-form-element');
    if (formularioLogin) {
        formularioLogin.addEventListener('submit', (e) => {
            e.preventDefault();

            const datosLogin = {
                username: document.getElementById('login-user').value,
                password: document.getElementById('login-pass').value
            };

            fetch(`${API_BASE_URL}/LoginController`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(datosLogin)
            })
                .then(respuesta => {
                    // Si el status HTTP es distinto de 200, provocamos un error para ir al Catch
                    if (!respuesta.ok) throw new Error('Credenciales incorrectas');
                    return respuesta.json();
                })
                .then(datos => {
                    // Guardamos en el navegador la sesión persistente (quién soy y qué rol tengo)
                    localStorage.setItem('user_session', datos.username);
                    localStorage.setItem('user_rol', datos.rol);
                    // Redirigimos al mapa principal
                    window.location.href = 'mapa.html';
                })
                .catch(error => {
                    alert('Usuario o contraseña incorrectos.');
                });
        });
    }

    // Lógica para el formulario de REGISTRO
    const formularioRegistro = document.getElementById('register-form-element');
    if (formularioRegistro) {
        formularioRegistro.addEventListener('submit', (e) => {
            e.preventDefault();

            const datosRegistro = {
                username: document.getElementById('reg-user').value,
                email: document.getElementById('reg-email').value,
                password: document.getElementById('reg-pass').value
            };

            fetch(`${API_BASE_URL}/RegisterController`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(datosRegistro)
            })
                .then(respuesta => {
                    if (!respuesta.ok) throw new Error('El usuario ya existe');
                    return respuesta.json();
                })
                .then(datos => {
                    // 1. AUTO-LOGIN: Cogemos el nombre que acaba de escribir y lo guardamos
                    const nombreNuevo = document.getElementById('reg-user').value;
                    localStorage.setItem('user_session', nombreNuevo);
                    localStorage.setItem('user_rol', 'usuario'); // Por defecto, es un usuario normal

                    // 2. REDIRECCIÓN DIRECTA AL MAPA
                    alert('Registro completado. ¡Bienvenido a Hyrule!');
                    window.location.href = 'mapa.html';
                }).catch(error => {
                    alert('Error al registrar. Es posible que el nombre o email ya estén en uso.');
                });
        });
    }

    // ==========================================
    // 7. MODALES SECUNDARIOS Y PERFIL
    // ==========================================

    // Ventana de "Recuperar Contraseña"
    const modalRecuperacion = document.getElementById('recovery-modal');
    const btnAbrirRecuperacion = document.getElementById('open-recovery-modal');
    const btnCerrarRecuperacion = document.getElementById('close-recovery-modal');
    const formularioRecuperacion = document.getElementById('recovery-form');

    if (modalRecuperacion && btnAbrirRecuperacion && btnCerrarRecuperacion && formularioRecuperacion) {
        btnAbrirRecuperacion.addEventListener('click', (e) => {
            e.preventDefault();
            modalRecuperacion.classList.remove('hidden');
        });
        btnCerrarRecuperacion.addEventListener('click', () => modalRecuperacion.classList.add('hidden'));
        modalRecuperacion.addEventListener('click', (e) => {
            if (e.target === modalRecuperacion) modalRecuperacion.classList.add('hidden');
        });
        formularioRecuperacion.addEventListener('submit', (e) => {
            e.preventDefault();
            alert('Enlace de recuperación enviado.');
            modalRecuperacion.classList.add('hidden');
            formularioRecuperacion.reset();
        });
    }

    // Lógica del Menú de Usuario (Cerrar sesión y Cambiar contraseña)
    const zonaAutenticacion = document.getElementById('auth-zone');
    const zonaPerfil = document.getElementById('profile-zone');
    const disparadorMenuPerfil = document.getElementById('profile-menu-trigger');
    const desplegablePerfil = document.getElementById('profile-dropdown');
    const btnCambiarPass = document.getElementById('btn-change-pass');
    const btnCerrarSesion = document.getElementById('btn-logout');
    const modalPassword = document.getElementById('password-modal');
    const btnCerrarModalPassword = document.getElementById('close-password-modal');
    const formularioPassword = document.getElementById('password-form');

    if (zonaAutenticacion && zonaPerfil && disparadorMenuPerfil && desplegablePerfil && btnCambiarPass && btnCerrarSesion && modalPassword && btnCerrarModalPassword && formularioPassword) {

        // Comprobamos si el usuario ya inició sesión leyendo el LocalStorage
        const usuarioActivo = localStorage.getItem('user_session');
        if (usuarioActivo) {
            // Ocultamos los botones de inicio/registro y mostramos la foto de perfil con su nombre
            zonaAutenticacion.classList.add('hidden');
            zonaPerfil.classList.remove('hidden');
            zonaPerfil.querySelector('.username-display').textContent = usuarioActivo;
        }

        // Mostrar/Ocultar el submenú del perfil al pulsar
        disparadorMenuPerfil.addEventListener('click', (e) => {
            e.stopPropagation(); // Evitamos que el clic cierre el menú automáticamente
            desplegablePerfil.classList.toggle('hidden');
        });

        // Click-Outside: Si pulsas fuera del menú, se esconde
        document.addEventListener('click', () => {
            desplegablePerfil.classList.add('hidden');
        });

        // Botón CERRAR SESIÓN
        btnCerrarSesion.addEventListener('click', () => {
            if (confirm('¿Quieres cerrar sesión?')) {
                // Borramos las variables del almacenamiento del navegador
                localStorage.removeItem('user_session');
                localStorage.removeItem('user_rol');
                // Devolvemos al usuario a la pantalla de inicio principal
                window.location.href = 'index.html';
            }
        });

        // Botón CAMBIAR CONTRASEÑA (Abre Modal)
        btnCambiarPass.addEventListener('click', () => modalPassword.classList.remove('hidden'));
        btnCerrarModalPassword.addEventListener('click', () => modalPassword.classList.add('hidden'));
        modalPassword.addEventListener('click', (e) => {
            if (e.target === modalPassword) modalPassword.classList.add('hidden');
        });
        // Eliminado evento duplicado que vaciaba el formulario antes del fetch
    }
    // =========================================
    // 1. CAMBIAR CONTRASEÑA 
    // =========================================
    const formularioCambiarPass = document.getElementById('password-form'); // Actualizado a tu HTML
    if (formularioCambiarPass) {
        formularioCambiarPass.addEventListener('submit', function (e) {
            e.preventDefault();

            const nuevaPass = document.getElementById('new-password-input').value; // Actualizado a tu HTML
            const usernameActual = localStorage.getItem('user_session');

            if (!usernameActual) {
                alert('Error: No has iniciado sesión.');
                return;
            }

            fetch(`${API_BASE_URL}/CambiarPassword`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: 'username=' + encodeURIComponent(usernameActual) + '&new_password=' + encodeURIComponent(nuevaPass)
            })
                .then(respuesta => respuesta.json())
                .then(datos => {
                    if (datos.success) {
                        alert('¡La contraseña ha sido modificada con éxito!');
                        formularioCambiarPass.reset();
                        document.getElementById('password-modal').classList.add('hidden');
                    } else {
                        alert('Error al intentar actualizar la contraseña en la base de datos.');
                    }
                })
                .catch(err => console.error('Error:', err));
        });
    }

    // =========================================
    // 2. BORRAR CUENTA 
    // =========================================
    const btnBorrarCuenta = document.getElementById('btn-delete-account');
    if (btnBorrarCuenta) {
        btnBorrarCuenta.addEventListener('click', () => {
            const seguro = confirm('⚠️ ¿Estás seguro de que quieres borrar tu cuenta? Esta acción es definitiva.');

            if (seguro) {
                const usernameActual = localStorage.getItem('user_session');

                fetch(`${API_BASE_URL}/BorrarUsuario`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: 'username=' + encodeURIComponent(usernameActual)
                })
                    .then(respuesta => respuesta.json())
                    .then(datos => {
                        if (datos.success) {
                            localStorage.removeItem('user_session');
                            localStorage.removeItem('user_rol');
                            alert('Tu cuenta ha sido eliminada para siempre de la base de datos.');
                            window.location.href = 'index.html';
                        } else {
                            alert('Error: No se pudo eliminar la cuenta del servidor.');
                        }
                    })
                    .catch(err => console.error('Error:', err));
            }
        });

    }
});

