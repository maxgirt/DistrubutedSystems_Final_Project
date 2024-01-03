import socket
import sys
from transformers import pipeline

def main():
    host = '0.0.0.0'
    port = 65432        # Port to listen on (non-privileged ports are > 1023)

    # Load Hugging Face model
    #model = pipeline('sentiment-analysis')

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind((host, port))
        s.listen()
        conn, addr = s.accept()
        with conn:
            print(f"Connected by {addr}")
            while True:
                data = conn.recv(1024)
                if not data:
                    break
                #prediction = model(data.decode('utf-8'))
                conn.sendall("Hello world")#str(prediction).encode('utf-8'))

if __name__ == "__main__":
    main()
