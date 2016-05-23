/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer;

import com.google.android.exoplayer.audio.AudioTrack;
import com.google.android.exoplayer.util.Assertions;

import android.os.Handler;
import android.os.SystemClock;

/**
 * Interface definition for a callback to be notified of audio {@link TrackRenderer} events.
 */
public interface AudioTrackRendererEventListener {

  /**
   * Invoked to pass the codec counters when the renderer is enabled.
   *
   * @param counters CodecCounters object used by the renderer.
   */
  void onAudioCodecCounters(CodecCounters counters);

  /**
   * Invoked when a decoder is created.
   *
   * @param decoderName The decoder that was created.
   * @param initializedTimestampMs {@link SystemClock#elapsedRealtime()} when initialization
   *     finished.
   * @param initializationDurationMs The time taken to initialize the decoder in milliseconds.
   */
  void onAudioDecoderInitialized(String decoderName, long initializedTimestampMs,
      long initializationDurationMs);

  /**
   * Invoked when an {@link AudioTrack} underrun occurs.
   *
   * @param bufferSize The size of the {@link AudioTrack}'s buffer, in bytes.
   * @param bufferSizeMs The size of the {@link AudioTrack}'s buffer, in milliseconds, if it is
   *     configured for PCM output. -1 if it is configured for passthrough output, as the buffered
   *     media can have a variable bitrate so the duration may be unknown.
   * @param elapsedSinceLastFeedMs The time since the {@link AudioTrack} was last fed data.
   */
  void onAudioTrackUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs);

  /**
   * Dispatches events to a {@link AudioTrackRendererEventListener}.
   */
  final class EventDispatcher {

    private final Handler handler;
    private final AudioTrackRendererEventListener listener;

    public EventDispatcher(Handler handler, AudioTrackRendererEventListener listener) {
      this.handler = listener != null ? Assertions.checkNotNull(handler) : null;
      this.listener = listener;
    }

    public void codecCounters(final CodecCounters codecCounters) {
      if (listener != null) {
        handler.post(new Runnable() {
          @Override
          public void run() {
            listener.onAudioCodecCounters(codecCounters);
          }
        });
      }
    }

    public void decoderInitialized(final String decoderName,
        final long initializedTimestampMs, final long initializationDurationMs) {
      if (listener != null) {
        handler.post(new Runnable() {
          @Override
          public void run() {
            listener.onAudioDecoderInitialized(decoderName, initializedTimestampMs,
                initializationDurationMs);
          }
        });
      }
    }

    public void audioTrackUnderrun(final int bufferSize, final long bufferSizeMs,
        final long elapsedSinceLastFeedMs) {
      if (listener != null) {
        handler.post(new Runnable()  {
          @Override
          public void run() {
            listener.onAudioTrackUnderrun(bufferSize, bufferSizeMs, elapsedSinceLastFeedMs);
          }
        });
      }
    }

  }

}