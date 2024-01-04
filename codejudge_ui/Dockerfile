FROM node:latest as build
WORKDIR /app
COPY package.json /app
RUN npm install --legacy-peer-deps
COPY . /app
RUN npm run build --legacy-peer-deps


FROM nginx:1-alpine-slim
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80
EXPOSE 3000
CMD ["nginx", "-g", "daemon off;"]
