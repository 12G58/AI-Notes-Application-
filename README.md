# AI assisted note taking application 
Written in kotlin, SQLite database, integrated with Gemma using Huggingface's Inference API and a local Flask server. The app accepts input in the form of images and extracts text from them via a huggingface image to text model (variable, depending on server availability) passes them through Gemma to display a better result to the user. 

While I had specifically created this to recognize handwritten mathematical and scientific equations, and generate real time notes using these images, the reachability of the model servers required for this proved to be a challenge. Might work on creating an ML model that does the job myself. 
