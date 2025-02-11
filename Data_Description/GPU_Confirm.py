import tensorflow as tf
print("GPU Available:", tf.config.list_physical_devices('GPU'))
import torch
print("GPU Available:", torch.cuda.is_available())
print("GPU Name:", torch.cuda.get_device_name(0))