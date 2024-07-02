
from flask import Flask, request, jsonify
import requests

app = Flask(__name__)

API_URL = "https://api-inference.huggingface.co/models/google/gemma-7b" #replace with a good fine-tuned LLM for Science/Math/Bio etc.
headers = {"Authorization": "Bearer hf_token"}


def query(payload):
    response = requests.post(API_URL, headers=headers, json=payload, timeout=120)
    return response.json()

@app.route("/", methods=["GET"])
def index():
    return "Welcome to a free huggingface-gemma (inference) API"


@app.route("/summarize", methods=["POST"])
def summarize():
    data = request.get_json()
    input_text = data.get("inputText", "")
    print(input_text)

    payload = {
        "inputs": input_text,
    }

    retry = 1
    try:
        output = query(payload)
        return jsonify(output)
        retry = 0

    except requests.exceptions.RequestException as e:
        return jsonify({"error": str(e)}), 500

    # Retry logic if model is still loading
    retry_attempts = 3
    retry_wait_time = 20  # seconds

    if retry == 1:
        for attempt in range(retry_attempts):
            try:
                output = query(payload)
                return jsonify(output)
                break  # Exit loop if successful


            except requests.exceptions.RequestException as e:
                print(f"Attempt {attempt + 1} failed. Retrying in {retry_wait_time} seconds.")
                time.sleep(retry_wait_time)
        else:
            jsonify({"error": "Failed after multiple attempts. Model might still be loading."}), 500

    return jsonify({"error": "Unexpected error occurred"}), 500


if __name__ == '__main__':
    app.run(host='192.168.1.19', debug=True)

# output = query({
#     "inputs": "Can you please let us know more details about your ",
# })
#
# print(output)
