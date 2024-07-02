from flask import Flask, request, jsonify
import requests
import time

app = Flask(__name__)

API_URL = "https://api-inference.huggingface.co/models/microsoft/trocr-base-handwritten" #replace with a good handwritten math/science equation detection model (ex. math2pix)
headers = {"Authorization": "Bearer hf_token"}


def query(filename, max_retries=5, retry_delay=10):
    with open(filename, "rb") as f:
        data = f.read()

    retries = 0
    while retries < max_retries:
        response = requests.post(API_URL, headers=headers, data=data)
        if response.status_code == 200:
            return response.json()
        elif response.status_code == 503:
            print(f"Service unavailable, retrying in {retry_delay} seconds...")
            time.sleep(retry_delay)
            retries += 1
            retry_delay *= 2
        elif response.status_code == 500:
            print(f"Server error (500), retrying in {retry_delay} seconds...")
            time.sleep(retry_delay)
            retries += 1
            retry_delay *= 2
        elif response.status_code == 400:
            error_message = response.json().get('error', 'Unknown error')
            print(f"Bad request (400): {error_message}. Retrying in {retry_delay} seconds...")
            time.sleep(retry_delay)
            retries += 1
            retry_delay *= 2
        else:
            return {"error": f"Unexpected error: {response.status_code}"}

    return {"error": "Max retries exceeded, could not get a successful response"}


@app.route('/detect', methods=['POST'])
def detect():
    if 'file' not in request.files:
        return jsonify({"error": "No file provided"}), 400

    file = request.files['file']
    if file.filename == '':
        return jsonify({"error": "No selected file"}), 400

    try:
        # result = query(file)
        # return jsonify(result)
        result = query(file)

        # Pass the result to the second Flask server
        second_server_response = requests.post('http://192.168.1.19:5000', json=result)
        if second_server_response.status_code == 200:
            return jsonify(second_server_response.json())
        else:
            return jsonify({"error": "Failed to process with second server",
                            "details": second_server_response.text}), second_server_response.status_code

    except Exception as e:
        return jsonify({"error": str(e)}), 500


if __name__ == '__main__':
    app.run(debug=True)
