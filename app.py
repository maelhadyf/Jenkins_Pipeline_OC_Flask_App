from flask import Flask, jsonify

app = Flask(__name__)

@app.route('/')
def home():
    return "Hello king memo, Hope you are well"

# Add health check endpoint here
@app.route('/health')
def health_check():
    return jsonify({"status": "healthy"}), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
