# eticketia-backend

Automatic ticket scanning

# Entorno de desarrollo

## Git Hooks y Conventional Commits

Este proyecto utiliza **Git Hooks** para asegurar la calidad y consistencia de los mensajes de commit, siguiendo las *
*Conventional Commits Rules**. Esto ayuda a mantener un historial de commits limpio y legible, facilitando la
automatización de la generación de changelogs y versiones.

Necesitarás tener **Node.js y npm** instalado en tu sistema para gestionar estas dependencias.
Una vez que estés en la **raíz del repositorio clonado**, ejecuta el siguiente comando:

```bash
npm install
```

### Conventional Commits

- ```feat: Descripción de la feature```
- ```fix: Correción realizada```
- ```docs: documentación actualizada```
- ```style: formateado o aplicación de un linter```
- ```refactor: refactorizado de código```
- ```test: creación de tests o utilidades```
- ```chore: mantenimiento o configuración del proyecto```
- ```build: gestión de dependencias o build del proyecto```
- ```ci: configuración de CI/CD```
- ```perf: optimizaciones de rendimiento```
- ```revert: deshacer commits previos.``` (Usar el mismo mensaje de commit que el commit revertido)

